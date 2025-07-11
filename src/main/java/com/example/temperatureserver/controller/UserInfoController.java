package com.example.temperatureserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.temperatureserver.model.UserInfo;
import com.example.temperatureserver.repository.UserInfoRepository;

@RestController
@RequestMapping("/api/user-info")
@CrossOrigin(origins = "http://localhost:5173")
public class UserInfoController {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @PostMapping
    public ResponseEntity<?> createUserInfo(@RequestBody UserInfo userInfo) {
        System.out.println(">>> REÇU REQUÊTE POST /api/user-info");
        System.out.println(">>> Contenu reçu : " + userInfo);

        // Validation des champs obligatoires
        if (userInfo.getProfession() == null || userInfo.getSecteur() == null || userInfo.getFrequence() == null) {
            System.out.println(">>> Profession, secteur ou fréquence manquant !");
            return ResponseEntity.badRequest().body("Profession, secteur et fréquence sont obligatoires");
        }

        // Gestion des listes nulles (évite des erreurs Hibernate)
        if (userInfo.getEspacesUtilises() == null) {
            userInfo.setEspacesUtilises(java.util.Collections.emptyList());
        }
        if (userInfo.getEquipementsPreferes() == null) {
            userInfo.setEquipementsPreferes(java.util.Collections.emptyList());
        }

        UserInfo saved = userInfoRepository.save(userInfo);
        System.out.println(">>> User enregistré avec ID : " + saved.getId());

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<String> testGet() {
        System.out.println(">>> REÇU REQUÊTE GET /api/user-info");
        return ResponseEntity.ok("API user-info OK");
    }
}
