package com.example.LFinder.service;

import com.example.LFinder.dto.MessageRequest;
import com.example.LFinder.model.Match;
import com.example.LFinder.model.Message;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.MatchRepository;
import com.example.LFinder.repository.MessageRepository;
import com.example.LFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MessageRepository messageRepository;

    // Obtiene los mensajes de la conversación (tal como se hacía en ConversationController)
    public List<Message> getMessages(User currentUser, Integer partnerId) {
        User partner = userRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Optional<Match> matchOpt = matchRepository.findMatchByUsers(currentUser.getIdUser(), partner.getIdUser());
        if (matchOpt.isPresent()) {
            return messageRepository.findByMatchIdOrderBySendDateAsc(matchOpt.get().getIdMatch());
        }
        return new ArrayList<>();
    }

    // Envía un mensaje, validando la existencia del match
    public void sendMessage(MessageRequest messageRequest, User currentUser) {
        User partner = userRepository.findById(messageRequest.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Optional<Match> matchOpt = matchRepository.findMatchByUsers(currentUser.getIdUser(), partner.getIdUser());
        if (!matchOpt.isPresent()) {
            throw new RuntimeException("No se encontró un match para la conversación");
        }
        Match match = matchOpt.get();
        Message message = new Message();
        message.setMatch(match);
        message.setSender(currentUser);
        message.setReceiver(partner);
        message.setContent(messageRequest.getContent());
        message.setSendDate(LocalDateTime.now());
        messageRepository.save(message);
    }
}
