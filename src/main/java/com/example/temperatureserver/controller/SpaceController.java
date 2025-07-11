package com.example.temperatureserver.controller;

import com.example.temperatureserver.model.Space;
import com.example.temperatureserver.repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spaces")
public class SpaceController {

    @Autowired
    private SpaceRepository spaceRepository;

    @GetMapping
    public List<Space> getAllSpaces() {
        return spaceRepository.findAll();
    }

    @PostMapping
    public Space createSpace(@RequestBody Space space) {
        return spaceRepository.save(space);
    }

    @DeleteMapping("/{id}")
    public void deleteSpace(@PathVariable Long id) {
        spaceRepository.deleteById(id);
    }
}
