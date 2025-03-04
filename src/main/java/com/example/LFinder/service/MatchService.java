package com.example.LFinder.service;

import com.example.LFinder.dto.UserDTO;
import com.example.LFinder.model.Match;
import com.example.LFinder.model.User;
import com.example.LFinder.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    public List<UserDTO> getMatches(User currentUser) {
        Integer currentUserId = currentUser.getIdUser();
        List<Match> matches = matchRepository.findByUser1_IdUserOrUser2_IdUser(currentUserId, currentUserId);
        List<UserDTO> matchedUsers = new ArrayList<>();
        for (Match match : matches) {
            if (match.getUser1().getIdUser().equals(currentUserId)) {
                matchedUsers.add(new UserDTO(match.getUser2().getIdUser(), match.getUser2().getUsername(), match.getUser2().getProfilePicture()));
            } else {
                matchedUsers.add(new UserDTO(match.getUser1().getIdUser(), match.getUser1().getUsername(), match.getUser1().getProfilePicture()));
            }
        }
        return matchedUsers;
    }
}

