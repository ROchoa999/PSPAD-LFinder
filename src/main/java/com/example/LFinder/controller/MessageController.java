package com.example.LFinder.controller;

import com.example.LFinder.dto.MessageRequest;
import com.example.LFinder.model.Message;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.UserRepository;
import com.example.LFinder.service.MessageService;
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
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    // Endpoint para mostrar la lista de mensajes directos
    @GetMapping("/md")
    public String directMessages(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<User> conversationPartners = messageService.getConversationPartners(currentUser.getIdUser());
        model.addAttribute("conversationPartners", conversationPartners);
        return "md";
    }

    // Endpoint para mostrar la conversación entre el usuario actual y el usuario con id 'userId'
    @GetMapping("/conversation")
    public String conversation(@RequestParam("userId") Integer userId, Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        User partner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("partner", partner);
        model.addAttribute("currentUser", currentUser.getUsername());
        List<Message> messages = messageService.getMessages(currentUser, partner.getIdUser());
        model.addAttribute("messages", messages);
        return "conversation";
    }

    // Endpoint para enviar un mensaje en la conversación
    @PostMapping("/conversation/send")
    public String sendMessage(@ModelAttribute MessageRequest messageRequest, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        messageService.sendMessage(messageRequest, currentUser);
        return "redirect:/conversation?userId=" + messageRequest.getPartnerId();
    }
}
