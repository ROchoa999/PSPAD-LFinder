package com.example.LFinder.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping("/available")
    public List<UserDTO> getAvailableUsers(@RequestParam(defaultValue = "10") int limit, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return userRepository.findAvailableUsers(currentUser.getIdUser(), PageRequest.of(0, limit));
    }

    @PostMapping("/action")
    public ResponseEntity<ActionResponse> submitAction(@RequestBody ActionRequest actionRequest, Authentication authentication) {
        User sender = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        User receiver = userRepository.findById(actionRequest.getTargetUserId())
                .orElseThrow(() -> new RuntimeException("Usuario objetivo no encontrado"));

        // Guardar la acción (like o dislike)
        Action action = new Action();
        action.setSender(sender);
        action.setReceiver(receiver);
        action.setLiked(actionRequest.getIsLike());
        action.setActionDate(LocalDateTime.now());
        actionRepository.save(action);

        // Respuesta por defecto (sin notificación)
        ActionResponse response = new ActionResponse("Acción registrada", false, null);

        // Si se envía un like, se verifica si se completa un match
        if (actionRequest.getIsLike()) {
            Optional<Action> reverseAction = actionRepository.findBySenderAndReceiverAndLiked(receiver, sender, true);
            if (reverseAction.isPresent()) {
                // Crear match
                Match match = new Match();
                match.setUser1(sender);
                match.setUser2(receiver);
                match.setMatchDate(LocalDateTime.now());
                // Notificar de inmediato al usuario que completa la acción
                match.setNotifiedUser1(true);
                // El otro usuario se notificará en su próxima interacción
                match.setNotifiedUser2(false);
                matchRepository.save(match);

                // Se podría notificar inmediatamente al sender, por ejemplo:
                response = new ActionResponse("Has hecho match con " + receiver.getUsername(), true, receiver.getUsername());
            }
        }

        // Verificar si el usuario actual tiene matches pendientes de notificación
        List<Match> pendingMatches = matchRepository.findPendingMatchesForUser(sender.getIdUser());
        if (!pendingMatches.isEmpty()) {
            StringBuilder sb = new StringBuilder("Matches anteriores:\n");
            for (Match pendingMatch : pendingMatches) {
                String matchedUsername;
                if (pendingMatch.getUser1().getIdUser().equals(sender.getIdUser())) {
                    matchedUsername = pendingMatch.getUser2().getUsername();
                    pendingMatch.setNotifiedUser1(true);
                } else {
                    matchedUsername = pendingMatch.getUser1().getUsername();
                    pendingMatch.setNotifiedUser2(true);
                }
                sb.append(matchedUsername).append("\n");
                matchRepository.save(pendingMatch);
            }
            // Actualizar la respuesta con el mensaje formateado
            response.setMessage(sb.toString());
            response.setMatch(true);
        }

        return ResponseEntity.ok(response);
    }

}
