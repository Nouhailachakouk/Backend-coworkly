import sqlite3
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
import json
import math
from datetime import datetime

# ========= 0) Connexion =========
conn = sqlite3.connect("mydata_ai.db", timeout=10.0)
conn.execute("PRAGMA journal_mode=WAL;")

# ========= 1) Capteurs =========
df = pd.read_sql_query("""
SELECT 
    sd.timestamp,
    sp.name as space_name,
    sp.type as space_type,
    sd.sensor_type,
    sd.value
FROM sensor_data sd
JOIN spaces sp ON sd.space_id = sp.id
WHERE sd.is_valid = 1
""", conn)

if df.empty:
    # Sécurité si table vide
    with open("ai_recommendations.json", "w", encoding="utf-8") as f:
        json.dump({"recommendations": [], "stats": {"message": "Aucune donnée capteur"}}, f, ensure_ascii=False, indent=2)
    raise SystemExit("Aucune donnée dans sensor_data.")

df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms', errors='coerce')
df = df.dropna(subset=['timestamp'])
df['timestamp_minute'] = df['timestamp'].dt.floor('min')

df_pivot = df.pivot_table(
    index=['timestamp_minute', 'space_name', 'space_type'],
    columns='sensor_type',
    values='value',
    aggfunc='mean'
).reset_index()

# Garantir les colonnes attendues
for col in ['OCCUPANCY', 'TEMPERATURE', 'HUMIDITY', 'CO2', 'LIGHT']:
    if col not in df_pivot.columns:
        df_pivot[col] = 0.0

df_pivot = df_pivot.fillna(0)

# ========= 2) Seuils =========
thresholds_by_space = {
    "Bureau privé": {
        "temp_min": 19, "temp_max": 22, "humidity_min": 40, "humidity_max": 60,
        "co2_max": 900, "light_min": 100, "light_max": 500, "occupancy_max": 2
    },
    "Espace ouvert": {
        "temp_min": 17, "temp_max": 30, "humidity_min": 40, "humidity_max": 60,
        "co2_max": 1100, "light_min": 500, "light_max": 2000, "occupancy_max": 30
    },
    "Salle de réunion": {
        "temp_min": 20, "temp_max": 24, "humidity_min": 40, "humidity_max": 60,
        "co2_max": 1000, "light_min": 500, "light_max": 1000, "occupancy_max": 10
    },
    "Cabine téléphonique 1": {
        "temp_min": 21, "temp_max": 23, "humidity_min": 50, "humidity_max": 70,
        "co2_max": 400, "light_min": 600, "light_max": 700, "occupancy_max": 1
    },
    "default": {
        "temp_min": 20, "temp_max": 28, "humidity_min": 40, "humidity_max": 60,
        "co2_max": 1000, "light_min": 100, "light_max": 1000, "occupancy_max": 25
    }
}

def normalize_space_type(space_type, space_name):
    if space_type and space_type.strip():
        st = space_type.lower()
        if "bureau privé" in st or "bureau prive" in st: return "Bureau privé"
        if "espace ouvert" in st: return "Espace ouvert"
        if "salle de réunion" in st or "salle de presentation" in st: return "Salle de réunion"
    if space_name:
        sn = space_name.lower()
        if "bureau privé" in sn or "bureau prive" in sn: return "Bureau privé"
        if "espace ouvert" in sn: return "Espace ouvert"
        if "salle de réunion" in sn or "salle de presentation" in sn: return "Salle de réunion"
    return "default"

def is_good_conditions(row):
    space_type_norm = normalize_space_type(row['space_type'], row['space_name'])
    t = thresholds_by_space.get(space_type_norm, thresholds_by_space['default'])
    return (
        t['temp_min'] <= row['TEMPERATURE'] <= t['temp_max'] and
        t['humidity_min'] <= row['HUMIDITY'] <= t['humidity_max'] and
        row['CO2'] < t['co2_max'] and
        t['light_min'] <= row['LIGHT'] <= t['light_max'] and
        row['OCCUPANCY'] <= t['occupancy_max']
    )

df_pivot['good_conditions'] = df_pivot.apply(is_good_conditions, axis=1).astype(int)

# ========= 3) Modèle simple pour score/diagnostic =========
X = df_pivot[['OCCUPANCY', 'TEMPERATURE', 'HUMIDITY', 'CO2', 'LIGHT']]
y = df_pivot['good_conditions']
model = RandomForestClassifier(random_state=42)
model.fit(X, y)

latest = (
    df_pivot.sort_values('timestamp_minute', ascending=False)
            .groupby('space_name')
            .first()
            .reset_index()
)

# ========= 4) Réservations (pics de demande) =========
try:
    reservations = pd.read_sql_query("""
    SELECT r.date, r.attendees, sp.name as space_name
    FROM reservations r
    JOIN spaces sp ON r.space_id = sp.id
    """, conn)
    reservations['date'] = pd.to_datetime(reservations['date'], errors='coerce')
    reservations = reservations.dropna(subset=['date'])
    reservations['day_of_week'] = reservations['date'].dt.day_name()
    total_res = len(reservations)
    by_day = reservations.groupby('day_of_week').size().sort_values(ascending=False)
    top_day = by_day.index[0] if not by_day.empty else None
    # 🔹 Traduction en français
    jours_fr = {
        'Monday': 'Lundi',
        'Tuesday': 'Mardi',
        'Wednesday': 'Mercredi',
        'Thursday': 'Jeudi',
        'Friday': 'Vendredi',
        'Saturday': 'Samedi',
        'Sunday': 'Dimanche'
    }
    if top_day in jours_fr:
        top_day = jours_fr[top_day]

    # % au-dessus de la moyenne
    avg_per_day = total_res / by_day.nunique() if by_day.nunique() else 0
    top_day_count = by_day.iloc[0] if not by_day.empty else 0
    demand_delta_pct = round(((top_day_count - avg_per_day) / avg_per_day) * 100, 1) if avg_per_day else 0.0
except Exception:
    top_day, demand_delta_pct = None, 0.0

# ========= 5) Préférences (user_info) =========
def safe_json_loads(s):
    try:
        val = json.loads(s) if isinstance(s, str) and s.strip() else []
        return val if isinstance(val, list) else []
    except Exception:
        return []

top_equipment = None
gender_prefs = {}
try:
    user_info = pd.read_sql_query("SELECT * FROM user_info", conn)
    if not user_info.empty:
        # équipements globaux
        all_equip = {}
        if 'equipements_preferes_json' in user_info.columns:
            for e in user_info['equipements_preferes_json'].dropna():
                for it in safe_json_loads(e):
                    all_equip[it] = all_equip.get(it, 0) + 1
        top_equipment = max(all_equip, key=all_equip.get) if all_equip else None

        # par genre (si colonne existe)
        if 'genre' in user_info.columns and 'equipements_preferes_json' in user_info.columns:
            for g, grp in user_info.groupby('genre'):
                loc = {}
                for e in grp['equipements_preferes_json'].dropna():
                    for it in safe_json_loads(e):
                        loc[it] = loc.get(it, 0) + 1
                # top 3 par genre
                gender_prefs[g] = sorted(loc.items(), key=lambda x: x[1], reverse=True)[:3]
except Exception:
    pass

# ========= 6) Génération “figure 2” =========
def priority_of(space_name, metric, value, t):
    """
    Règle simple de priorité en fonction de l'écart au seuil.
    - Haute : écart sévère (CO2 > 1.2*max, occupancy > max, |temp-ideal| > 2.5°C, lumière extrême)
    - Moyenne : écart modéré
    - Basse : ok
    """
    if metric == "CO2":
        if value > 1.2 * t['co2_max']: return "Priorité haute"
        if value > t['co2_max']: return "Priorité moyenne"
        return "Priorité faible"
    if metric == "OCCUPANCY":
        if value > t['occupancy_max'] * 1.1: return "Priorité haute"
        if value > t['occupancy_max']: return "Priorité moyenne"
        return "Priorité faible"
    if metric == "TEMPERATURE":
        ideal = (t['temp_min'] + t['temp_max']) / 2
        gap = abs(value - ideal)
        if gap > 2.5: return "Priorité haute"
        if gap > 1.0: return "Priorité moyenne"
        return "Priorité faible"
    if metric == "LIGHT":
        if value < t['light_min'] * 0.7 or value > t['light_max'] * 1.3: return "Priorité haute"
        if value < t['light_min'] or value > t['light_max']: return "Priorité moyenne"
        return "Priorité faible"
    if metric == "HUMIDITY":
        if value < t['humidity_min'] - 10 or value > t['humidity_max'] + 10: return "Priorité haute"
        if value < t['humidity_min'] or value > t['humidity_max']: return "Priorité moyenne"
        return "Priorité faible"
    return "Priorité faible"

def impact_text(metric, value, t):
    if metric == "CO2":
        over = max(0, value - t['co2_max'])
        return f"Impact prévu : +{min(20, round(over/50))}% de concentration perçue après amélioration de la ventilation."
    if metric == "OCCUPANCY":
        excess = max(0, value - t['occupancy_max'])
        return f"Impact prévu : -{min(30, excess*3)}% de bruit et meilleure sécurité."
    if metric == "TEMPERATURE":
        return "Impact prévu : amélioration du confort thermique et de la satisfaction."
    if metric == "LIGHT":
        return "Impact prévu : baisse de la fatigue visuelle et +productivité."
    if metric == "HUMIDITY":
        return "Impact prévu : confort respiratoire et santé perçue améliorés."
    return "Impact prévu : amélioration de l’expérience."

recommendations = []

# 6.a) Recos par espace (diagnostic capteurs)
for _, row in latest.iterrows():
    st = normalize_space_type(row['space_type'], row['space_name'])
    t = thresholds_by_space.get(st, thresholds_by_space['default'])

    features = pd.DataFrame([[
        row['OCCUPANCY'], row['TEMPERATURE'], row['HUMIDITY'], row['CO2'], row['LIGHT']
    ]], columns=['OCCUPANCY','TEMPERATURE','HUMIDITY','CO2','LIGHT'])

    pred = model.predict(features)[0]

    # Conditions idéales -> petite carte informative (facultatif)
    if pred == 1:
        recommendations.append({
            "title": f"Conditions stables — {row['space_name']}",
            "description": "Les indicateurs sont dans les plages optimales. Continuer la surveillance.",
            "priority": "Priorité faible",
            "impact": "Impact prévu : maintien du confort et de la productivité."
        })
        continue

    # Sinon, produire des cartes ciblées par métrique hors seuil
    checks = [
        ("CO2", row["CO2"], row["CO2"] > t["co2_max"], f"CO₂ élevé détecté ({int(row['CO2'])} ppm)"),
        ("OCCUPANCY", row["OCCUPANCY"], row["OCCUPANCY"] > t["occupancy_max"], f"Sur-occupation ({int(row['OCCUPANCY'])}/{t['occupancy_max']})"),
        ("TEMPERATURE", row["TEMPERATURE"], (row["TEMPERATURE"] < t["temp_min"] or row["TEMPERATURE"] > t["temp_max"]),
         f"Température hors plage ({round(row['TEMPERATURE'],1)}°C ; cible {t['temp_min']}-{t['temp_max']}°C)"),
        ("HUMIDITY", row["HUMIDITY"], (row["HUMIDITY"] < t["humidity_min"] or row["HUMIDITY"] > t["humidity_max"]),
         f"Humidité hors plage ({round(row['HUMIDITY'],1)}% ; cible {t['humidity_min']}-{t['humidity_max']}%)"),
        ("LIGHT", row["LIGHT"], (row["LIGHT"] < t["light_min"] or row["LIGHT"] > t["light_max"]),
         f"Luminosité à ajuster ({int(row['LIGHT'])} lx ; cible {t['light_min']}-{t['light_max']} lx)"),
    ]

    for metric, val, is_bad, desc in checks:
        if not is_bad:
            continue
        prio = priority_of(row['space_name'], metric, val, t)
        recommendations.append({
            "title": f"{desc} — {row['space_name']}",
            "description": {
                "CO2": "Augmenter le renouvellement d’air (ouvrir fenêtres, activer ventilation).",
                "OCCUPANCY": "Limiter le nombre de personnes ou répartir vers un autre espace.",
                "TEMPERATURE": "Ajuster le chauffage/climatisation pour revenir dans la plage cible.",
                "HUMIDITY": "Corriger l’humidité (humidificateur/déshumidification).",
                "LIGHT": "Adapter l’éclairage (intensité/points lumineux supplémentaires)."
            }[metric],
            "priority": prio,
            "impact": impact_text(metric, val, t)
        })

# 6.b) Reco “Ouverture supplémentaire” selon la demande
if top_day:
    # Carte globale type figure 2
    prio_global = "Priorité haute" if demand_delta_pct >= 15 else ("Priorité moyenne" if demand_delta_pct >= 5 else "Priorité faible")
    impact = f"Impact prévu : +{max(5, int(demand_delta_pct))}% de capacité absorbée" if demand_delta_pct > 0 else "Impact prévu : meilleure répartition de la charge"
    recommendations.append({
        "title": "Optimisation des créneaux",
        "description": f"Le {top_day} présente la plus forte demande ({int(top_day_count)} réservations, ~{demand_delta_pct}% au-dessus de la moyenne). Envisager l’ouverture d’un espace supplémentaire.",
        "priority": prio_global,
        "impact": impact
    })

# 6.c) Reco “Préférences utilisateurs”
if top_equipment:
    recommendations.append({
        "title": "Amélioration de l’expérience",
        "description": f"Forte préférence déclarée pour : {top_equipment}. Prioriser cet équipement dans les espaces ouverts et salles à forte fréquentation.",
        "priority": "Priorité moyenne",
        "impact": "Impact prévu : +0.2 à +0.4 sur l’indice de satisfaction."
    })

if gender_prefs:
    # Synthèse courte par genre
    for g, lst in gender_prefs.items():
        if not lst:
            continue
        items = ", ".join([k for k, _ in lst])
        recommendations.append({
            "title": f"Personnalisation par profil — {g}",
            "description": f"Équipements les plus plébiscités : {items}. Adapter l’offre pour ce segment.",
            "priority": "Priorité faible",
            "impact": "Impact prévu : meilleure adéquation offre/besoins par segment."
        })

# ========= 7) Sortie JSON structurée =========
stats = {
    "most_demand_day": top_day,
    "most_demand_day_delta_pct": demand_delta_pct,
    "top_equipment": top_equipment,
    "generated_at": datetime.utcnow().isoformat() + "Z"
}

output = {
    "recommendations": recommendations,
    "stats": stats
}

with open("ai_recommendations.json", "w", encoding="utf-8") as f:
    json.dump(output, f, ensure_ascii=False, indent=2)

print("✅ Recommandations 'figure 2' générées avec succès.")

