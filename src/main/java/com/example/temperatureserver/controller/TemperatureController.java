package com.example.temperatureserver.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.temperatureserver.model.Temperature;
import com.example.temperatureserver.repository.TemperatureRepository;

@RestController
@RequestMapping("/temperature")
public class TemperatureController {

    private final TemperatureRepository repo;

    public TemperatureController(TemperatureRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Temperature saveTemperature(@RequestBody Temperature temp) {
        return repo.save(temp);
    }

    @GetMapping
    public List<Temperature> getAll() {
        return repo.findAll();
    }
}
