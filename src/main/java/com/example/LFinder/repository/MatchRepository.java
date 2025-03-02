package com.example.LFinder.repository;

import com.example.LFinder.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByUser1_IdUserOrUser2_IdUser(Integer userId1, Integer userId2);
}
