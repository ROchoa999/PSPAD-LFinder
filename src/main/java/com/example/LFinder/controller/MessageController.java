package com.example.LFinder.controller;

import com.example.LFinder.model.User;
import com.example.LFinder.repository.MessageRepository;
import com.example.LFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/md")
    public String directMessages(Model model, Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Obtenemos la lista de usuarios con los que se ha tenido conversación
        List<User> conversationPartners = messageRepository.findConversationPartners(currentUser.getIdUser());
        model.addAttribute("conversationPartners", conversationPartners);
        return "md"; // Renderiza el template md.html
    }
}
