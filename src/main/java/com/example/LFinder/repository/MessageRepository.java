package com.example.LFinder.repository;

import com.example.LFinder.model.Message;
import com.example.LFinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("SELECT m.receiver FROM Message m WHERE m.sender.idUser = :userId")
    List<User> findReceiversBySender(@Param("userId") Integer userId);

    @Query("SELECT m.sender FROM Message m WHERE m.receiver.idUser = :userId")
    List<User> findSendersByReceiver(@Param("userId") Integer userId);

    // Método por defecto para combinar ambas listas sin duplicados
    default List<User> findConversationPartners(@Param("userId") Integer userId) {
        List<User> receivers = findReceiversBySender(userId);
        List<User> senders = findSendersByReceiver(userId);
        Set<User> partners = new HashSet<>();
        if (receivers != null) {
            partners.addAll(receivers);
        }
        if (senders != null) {
            partners.addAll(senders);
        }
        return new ArrayList<>(partners);
    }

    @Query("SELECT m FROM Message m WHERE m.match.idMatch = :matchId ORDER BY m.sendDate ASC")
    List<Message> findByMatchIdOrderBySendDateAsc(@Param("matchId") Integer matchId);
}
