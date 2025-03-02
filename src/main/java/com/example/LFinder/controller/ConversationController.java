package com.example.LFinder.controller;

import com.example.LFinder.dto.MessageRequest;
import com.example.LFinder.model.Match;
import com.example.LFinder.model.Message;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.MatchRepository;
import com.example.LFinder.repository.MessageRepository;
import com.example.LFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ConversationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/conversation")
    public String conversation(@RequestParam("userId") Integer userId, Model model, Authentication authentication) {
        // Obtener usuario actual y partner
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        User partner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("partner", partner);
        model.addAttribute("currentUser", currentUser.getUsername());

        // Buscar el match existente entre ambos usuarios
        Optional<Match> matchOpt = matchRepository.findMatchByUsers(currentUser.getIdUser(), partner.getIdUser());
        if (matchOpt.isPresent()) {
            Match match = matchOpt.get();
            List<Message> messages = messageRepository.findByMatchIdOrderBySendDateAsc(match.getIdMatch());
            model.addAttribute("messages", messages);
        } else {
            model.addAttribute("messages", new ArrayList<Message>());
        }
        return "conversation";
    }

    @PostMapping("/conversation/send")
    public String sendMessage(@ModelAttribute MessageRequest messageRequest, Authentication authentication) {
        // Obtener usuario actual y partner
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        User partner = userRepository.findById(messageRequest.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar el match para la conversación
        Optional<Match> matchOpt = matchRepository.findMatchByUsers(currentUser.getIdUser(), partner.getIdUser());
        if (!matchOpt.isPresent()) {
            throw new RuntimeException("No se encontró un match para la conversación");
        }
        Match match = matchOpt.get();

        // Crear y guardar el mensaje
        Message message = new Message();
        message.setMatch(match);
        message.setSender(currentUser);
        message.setReceiver(partner);
        message.setContent(messageRequest.getContent());
        message.setSendDate(LocalDateTime.now());
        messageRepository.save(message);

        // Redirigir a la conversación actualizada
        return "redirect:/conversation?userId=" + partner.getIdUser();
    }
}
