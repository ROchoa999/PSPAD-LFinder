package com.example.LFinder.controller;

import com.example.LFinder.dto.UserDTO;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.UserRepository;
import com.example.LFinder.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MatchesController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/matches")
    public String listMatches(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<UserDTO> matchedUsers = matchService.getMatches(currentUser);
        model.addAttribute("matchedUsers", matchedUsers);
        return "matches"; // Renderiza el template matches.html
    }
}

