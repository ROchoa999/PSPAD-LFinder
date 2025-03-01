package com.example.LFinder.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Integer idMatch;

    @ManyToOne
    @JoinColumn(name = "user_1_id", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user_2_id", nullable = false)
    private User user2;

    @Column(name = "match_date", nullable = false, updatable = false)
    private LocalDateTime matchDate;

    @Column(nullable = false)
    private Boolean notified;

}
