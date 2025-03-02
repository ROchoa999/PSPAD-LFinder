package com.example.LFinder.repository;

import com.example.LFinder.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    List<Match> findByUser1_IdUserOrUser2_IdUser(Integer userId1, Integer userId2);

    @Query("SELECT m FROM Match m WHERE (m.user1.idUser = :userId1 AND m.user2.idUser = :userId2) " +
            "OR (m.user1.idUser = :userId2 AND m.user2.idUser = :userId1)")
    Optional<Match> findMatchByUsers(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
}
