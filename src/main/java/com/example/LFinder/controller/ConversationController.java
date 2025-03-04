package com.example.LFinder.controller;

import com.example.LFinder.dto.MessageRequest;
import com.example.LFinder.model.Message;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.UserRepository;
import com.example.LFinder.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/conversation")
    public String conversation(@RequestParam("userId") Integer userId, Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        User partner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("partner", partner);
        model.addAttribute("currentUser", currentUser.getUsername());
        List<Message> messages = conversationService.getMessages(currentUser, partner.getIdUser());
        model.addAttribute("messages", messages);
        return "conversation";
    }

    @PostMapping("/conversation/send")
    public String sendMessage(@ModelAttribute MessageRequest messageRequest, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        conversationService.sendMessage(messageRequest, currentUser);
        return "redirect:/conversation?userId=" + messageRequest.getPartnerId();
    }
}
