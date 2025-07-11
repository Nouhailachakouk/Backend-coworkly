package com.example.temperatureserver.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;

@Entity
@Table(name = "user_info")
public class UserInfo {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String profession;
    private String secteur;

    @JsonProperty("frequences")
    private String frequence;

    @Column(name = "espaces_utilises_json", columnDefinition = "TEXT")
    private String espacesUtilisesJson;

    @JsonProperty("espacesUtilises")
    @Transient
    private List<String> espacesUtilises;

    @Column(name = "equipements_preferes_json", columnDefinition = "TEXT")
    private String equipementsPreferesJson;

    @JsonProperty("equipementsPref")
    @Transient
    private List<String> equipementsPreferes;

    @Column(length = 1000)
    private String suggestions;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Relation ManyToOne vers User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters & setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public String getSecteur() { return secteur; }
    public void setSecteur(String secteur) { this.secteur = secteur; }

    public String getFrequence() { return frequence; }
    public void setFrequence(String frequence) { this.frequence = frequence; }

    public List<String> getEspacesUtilises() {
        if (espacesUtilises == null && espacesUtilisesJson != null) {
            try {
                espacesUtilises = mapper.readValue(espacesUtilisesJson, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                espacesUtilises = null;
            }
        }
        return espacesUtilises;
    }

    public void setEspacesUtilises(List<String> espacesUtilises) {
        this.espacesUtilises = espacesUtilises;
        try {
            this.espacesUtilisesJson = mapper.writeValueAsString(espacesUtilises);
        } catch (Exception e) {
            this.espacesUtilisesJson = null;
        }
    }

    public List<String> getEquipementsPreferes() {
        if (equipementsPreferes == null && equipementsPreferesJson != null) {
            try {
                equipementsPreferes = mapper.readValue(equipementsPreferesJson, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                equipementsPreferes = null;
            }
        }
        return equipementsPreferes;
    }

    public void setEquipementsPreferes(List<String> equipementsPreferes) {
        this.equipementsPreferes = equipementsPreferes;
        try {
            this.equipementsPreferesJson = mapper.writeValueAsString(equipementsPreferes);
        } catch (Exception e) {
            this.equipementsPreferesJson = null;
        }
    }

    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Optionnel : sérialiser avant persistance (si tu préfères ce pattern)
    @PrePersist
    @PreUpdate
    public void serializeLists() {
        try {
            this.espacesUtilisesJson = mapper.writeValueAsString(espacesUtilises);
            this.equipementsPreferesJson = mapper.writeValueAsString(equipementsPreferes);
        } catch (Exception e) {
            this.espacesUtilisesJson = null;
            this.equipementsPreferesJson = null;
        }
    }
}
