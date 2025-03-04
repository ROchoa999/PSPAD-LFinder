package com.example.LFinder.controller;

import com.example.LFinder.dto.ActionRequest;
import com.example.LFinder.dto.ActionResponse;
import com.example.LFinder.dto.UserDTO;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.UserRepository;
import com.example.LFinder.service.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscoveryService discoveryService;

    @GetMapping("/available")
    public List<UserDTO> getAvailableUsers(@RequestParam(defaultValue = "10") int limit, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return discoveryService.getAvailableUsers(currentUser, limit);
    }

    @PostMapping("/action")
    public ResponseEntity<ActionResponse> submitAction(@RequestBody ActionRequest actionRequest, Authentication authentication) {
        User sender = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        ActionResponse response = discoveryService.processAction(sender, actionRequest);
        return ResponseEntity.ok(response);
    }
}

