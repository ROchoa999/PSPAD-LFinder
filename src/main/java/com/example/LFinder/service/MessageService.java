package com.example.LFinder.service;

import com.example.LFinder.model.User;
import com.example.LFinder.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<User> getConversationPartners(Integer userId) {
        return messageRepository.findConversationPartners(userId);
    }
}

