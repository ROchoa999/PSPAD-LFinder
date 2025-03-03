package com.example.LFinder.repository;

import com.example.LFinder.dto.UserDTO;
import com.example.LFinder.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Query("SELECT new com.example.LFinder.dto.UserDTO(u.idUser, u.username, u.profilePicture) " +
            "FROM User u " +
            "WHERE u.idUser <> :currentUserId " +
            "AND u.idUser NOT IN (SELECT a.receiver.idUser FROM Action a WHERE a.sender.idUser = :currentUserId) " +
            "AND u.idUser NOT IN (SELECT a.sender.idUser FROM Action a WHERE a.receiver.idUser = :currentUserId AND a.liked = false)")
    List<UserDTO> findAvailableUsers(@Param("currentUserId") Integer currentUserId, Pageable pageable);

}
