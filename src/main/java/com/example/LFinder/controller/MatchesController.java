package com.example.LFinder.controller;

import com.example.LFinder.dto.UserDTO;
import com.example.LFinder.model.Match;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.MatchRepository;
import com.example.LFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MatchesController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping("/matches")
    public String listMatches(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Integer currentUserId = currentUser.getIdUser();

        // Obtener todos los matches en los que participa el usuario actual
        List<Match> matches = matchRepository.findByUser1_IdUserOrUser2_IdUser(currentUserId, currentUserId);

        List<UserDTO> matchedUsers = new ArrayList<>();
        // Para cada match, determinamos el usuario opuesto
        for (Match match : matches) {
            if (match.getUser1().getIdUser().equals(currentUserId)) {
                matchedUsers.add(new UserDTO(match.getUser2().getIdUser(), match.getUser2().getUsername(), match.getUser2().getProfilePicture()));
            } else {
                matchedUsers.add(new UserDTO(match.getUser1().getIdUser(), match.getUser1().getUsername(), match.getUser1().getProfilePicture()));
            }
        }
        model.addAttribute("matchedUsers", matchedUsers);
        return "matches"; // Renderiza el template matches.html
    }
}
