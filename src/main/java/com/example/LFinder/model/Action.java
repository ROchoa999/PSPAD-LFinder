package com.example.LFinder.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "actions")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Integer idAction;

    @ManyToOne
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    @Column(name = "is_like", nullable = false)
    private Boolean liked; // Campo renombrado de isLike a liked

    @Column(name = "action_date", nullable = false, updatable = false)
    private LocalDateTime actionDate;
}
