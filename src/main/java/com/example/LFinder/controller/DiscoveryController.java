package com.example.LFinder.controller;

import com.example.LFinder.dto.UserDTO;
import com.example.LFinder.dto.ActionRequest;
import com.example.LFinder.model.Action;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.ActionRepository;
import com.example.LFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActionRepository actionRepository;

    @GetMapping("/available")
    public List<UserDTO> getAvailableUsers(@RequestParam(defaultValue = "10") int limit, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return userRepository.findAvailableUsers(currentUser.getIdUser(), PageRequest.of(0, limit));
    }

    @PostMapping("/action")
    public String submitAction(@RequestBody ActionRequest actionRequest, Authentication authentication) {
        User sender = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        User receiver = userRepository.findById(actionRequest.getTargetUserId())
                .orElseThrow(() -> new RuntimeException("Usuario objetivo no encontrado"));

        Action action = new Action();
        action.setSender(sender);
        action.setReceiver(receiver);
        action.setIsLike(actionRequest.getIsLike());
        action.setActionDate(LocalDateTime.now());
        actionRepository.save(action);
        return "Acción registrada";
    }
}
