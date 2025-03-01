package com.example.LFinder.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer idUser;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "profile_picture", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String profilePicture = "FinderLogo.png";

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Action> sentActions;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Action> receivedActions;

    @OneToMany(mappedBy = "user1", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Match> user1Match;

    @OneToMany(mappedBy = "user2", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Match> user2Match;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Message> receivedMessages;

}
