package com.example.LFinder.repository;

import com.example.LFinder.model.Action;
import com.example.LFinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<Action, Integer> {
    Optional<Action> findBySenderAndReceiverAndLiked(User sender, User receiver, Boolean liked);
}
