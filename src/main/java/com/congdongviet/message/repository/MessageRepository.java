package com.congdongviet.message.repository;

import com.congdongviet.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    long countByConversationIdAndReadFalseAndSenderIdNot(UUID conversationId, UUID userId);

    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.conversation.id = :convId AND m.sender.id != :userId AND m.read = false")
    void markAsRead(@Param("convId") UUID conversationId, @Param("userId") UUID userId);

    @Query("SELECT COUNT(DISTINCT m.conversation.id) FROM Message m " +
           "JOIN m.conversation c JOIN c.participants p " +
           "WHERE p.id = :userId AND m.sender.id != :userId AND m.read = false")
    long countUnreadConversations(@Param("userId") UUID userId);
}
