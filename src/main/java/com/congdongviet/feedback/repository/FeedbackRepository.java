package com.congdongviet.feedback.repository;

import com.congdongviet.feedback.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(Feedback.FeedbackStatus status);
}
