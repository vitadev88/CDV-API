package com.congdongviet.message.repository;

import com.congdongviet.message.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.id = :userId ORDER BY c.updatedAt DESC")
    List<Conversation> findByParticipantId(@Param("userId") UUID userId);

    @Query("SELECT c FROM Conversation c JOIN c.participants p1 JOIN c.participants p2 " +
           "WHERE p1.id = :user1 AND p2.id = :user2 AND c.listing.id = :listingId")
    Optional<Conversation> findByParticipantsAndListing(@Param("user1") UUID user1,
                                                         @Param("user2") UUID user2,
                                                         @Param("listingId") UUID listingId);
}
