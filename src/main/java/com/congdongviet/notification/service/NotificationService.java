package com.congdongviet.notification.service;

import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.common.exception.ResourceNotFoundException;
import com.congdongviet.notification.dto.NotificationResponse;
import com.congdongviet.notification.entity.Notification;
import com.congdongviet.notification.entity.NotificationType;
import com.congdongviet.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Tạo thông báo và push qua WebSocket.
     */
    @Transactional
    public void send(UUID userId, NotificationType type, String title, String content,
                     UUID referenceId, String referenceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .content(content)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .build();
        notificationRepository.save(notification);

        // Push qua WebSocket
        messagingTemplate.convertAndSend("/topic/notifications." + userId, toResponse(notification));
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        if (!n.getUser().getId().equals(userId)) {
            throw new com.congdongviet.common.exception.BadRequestException("Not your notification");
        }
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType().name())
                .title(n.getTitle())
                .content(n.getContent())
                .referenceId(n.getReferenceId())
                .referenceType(n.getReferenceType())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
