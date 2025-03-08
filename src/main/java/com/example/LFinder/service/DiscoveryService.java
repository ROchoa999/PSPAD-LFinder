package com.example.LFinder.service;

import com.example.LFinder.dto.ActionRequest;
import com.example.LFinder.dto.ActionResponse;
import com.example.LFinder.dto.UserDTO;
import com.example.LFinder.model.Action;
import com.example.LFinder.model.Match;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.ActionRepository;
import com.example.LFinder.repository.MatchRepository;
import com.example.LFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiscoveryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private MatchRepository matchRepository;

    // Devuelve los usuarios disponibles (igual que en DiscoveryController original)
    public List<UserDTO> getAvailableUsers(User currentUser, int limit) {
        return userRepository.findAvailableUsers(currentUser.getIdUser(), PageRequest.of(0, limit));
    }

    // Procesa la acción (like o dislike) y crea match en caso de correspondencia
    public ActionResponse processAction(User sender, ActionRequest actionRequest) {
        User receiver = userRepository.findById(actionRequest.getTargetUserId())
                .orElseThrow(() -> new RuntimeException("Usuario objetivo no encontrado"));

        Action action = new Action();
        action.setSender(sender);
        action.setReceiver(receiver);
        action.setLiked(actionRequest.getIsLike());
        action.setActionDate(LocalDateTime.now());
        actionRepository.save(action);

        // Usamos un StringBuilder para acumular los mensajes
        StringBuilder messageBuilder = new StringBuilder();
        boolean isMatch = false;
        String immediateMatchUsername = null;

        // Primero se evalúa el match inmediato si es un like
        if (actionRequest.getIsLike()) {
            Optional<Action> reverseAction = actionRepository.findBySenderAndReceiverAndLiked(receiver, sender, true);
            if (reverseAction.isPresent()) {
                Match match = new Match();
                match.setUser1(sender);
                match.setUser2(receiver);
                match.setMatchDate(LocalDateTime.now());
                match.setNotifiedUser1(true);
                match.setNotifiedUser2(false);
                matchRepository.save(match);
                messageBuilder.append("Has hecho match con ").append(receiver.getUsername());
                isMatch = true;
                immediateMatchUsername = receiver.getUsername();
            }
        }

        // Luego se evalúan los matches pendientes
        List<Match> pendingMatches = matchRepository.findPendingMatchesForUser(sender.getIdUser());
        if (!pendingMatches.isEmpty()) {
            // Si ya hay un mensaje (match inmediato), se agrega un salto de línea
            if (!messageBuilder.isEmpty()) {
                messageBuilder.append("\n\n");
            }
            messageBuilder.append("Matches anteriores:\n");
            for (Match pendingMatch : pendingMatches) {
                String matchedUsername;
                if (pendingMatch.getUser1().getIdUser().equals(sender.getIdUser())) {
                    matchedUsername = pendingMatch.getUser2().getUsername();
                    pendingMatch.setNotifiedUser1(true);
                } else {
                    matchedUsername = pendingMatch.getUser1().getUsername();
                    pendingMatch.setNotifiedUser2(true);
                }
                messageBuilder.append(matchedUsername).append("\n");
                matchRepository.save(pendingMatch);
            }
            isMatch = true;
        }

        String finalMessage = messageBuilder.length() > 0 ? messageBuilder.toString() : "Acción registrada";
        return new ActionResponse(finalMessage, isMatch, immediateMatchUsername);
    }

}

