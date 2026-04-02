package com.congdongviet.message.service;

import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.common.exception.ResourceNotFoundException;
import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.repository.ListingRepository;
import com.congdongviet.message.dto.ConversationResponse;
import com.congdongviet.message.dto.CreateConversationRequest;
import com.congdongviet.message.dto.MessageResponse;
import com.congdongviet.message.dto.SendMessageRequest;
import com.congdongviet.message.entity.Conversation;
import com.congdongviet.message.entity.Message;
import com.congdongviet.message.repository.ConversationRepository;
import com.congdongviet.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    @Transactional
    public ConversationResponse createConversation(UUID senderId, CreateConversationRequest request) {
        if (senderId.equals(request.getRecipientId())) {
            throw new BadRequestException("Cannot create conversation with yourself");
        }

        // Check if conversation already exists for this listing + pair
        var existing = conversationRepository.findByParticipantsAndListing(
                senderId, request.getRecipientId(), request.getListingId());
        if (existing.isPresent()) {
            // If initial message provided, send it
            if (request.getInitialMessage() != null && !request.getInitialMessage().isBlank()) {
                sendMessage(existing.get().getId(), senderId,
                        createSendRequest(request.getInitialMessage(), null));
            }
            return toConversationResponse(existing.get(), senderId);
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getRecipientId()));
        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", request.getListingId()));

        Conversation conversation = Conversation.builder()
                .listing(listing)
                .participants(Set.of(sender, recipient))
                .build();
        conversationRepository.save(conversation);

        // Send initial message if provided
        if (request.getInitialMessage() != null && !request.getInitialMessage().isBlank()) {
            Message msg = Message.builder()
                    .conversation(conversation)
                    .sender(sender)
                    .content(request.getInitialMessage())
                    .build();
            messageRepository.save(msg);
        }

        return toConversationResponse(conversation, senderId);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations(UUID userId) {
        return conversationRepository.findByParticipantId(userId).stream()
                .map(c -> toConversationResponse(c, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(UUID conversationId, UUID userId, Pageable pageable) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        validateParticipant(conv, userId);
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(this::toMessageResponse);
    }

    @Transactional
    public MessageResponse sendMessage(UUID conversationId, UUID senderId, SendMessageRequest request) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        validateParticipant(conv, senderId);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));

        Message msg = Message.builder()
                .conversation(conv)
                .sender(sender)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .build();
        messageRepository.save(msg);

        // Update conversation timestamp
        conv.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conv);

        return toMessageResponse(msg);
    }

    @Transactional
    public void markAsRead(UUID conversationId, UUID userId) {
        messageRepository.markAsRead(conversationId, userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return messageRepository.countUnreadConversations(userId);
    }

    private void validateParticipant(Conversation conv, UUID userId) {
        boolean isParticipant = conv.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));
        if (!isParticipant) {
            throw new BadRequestException("You are not a participant of this conversation");
        }
    }

    private SendMessageRequest createSendRequest(String content, String imageUrl) {
        SendMessageRequest req = new SendMessageRequest();
        req.setContent(content);
        req.setImageUrl(imageUrl);
        return req;
    }

    private ConversationResponse toConversationResponse(Conversation conv, UUID currentUserId) {
        List<ConversationResponse.ParticipantInfo> participants = conv.getParticipants().stream()
                .map(u -> ConversationResponse.ParticipantInfo.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .avatarUrl(u.getAvatarUrl())
                        .build())
                .toList();

        // Get last message
        Page<Message> lastMsgPage = messageRepository.findByConversationIdOrderByCreatedAtDesc(
                conv.getId(), Pageable.ofSize(1));
        String lastMsg = lastMsgPage.hasContent() ? lastMsgPage.getContent().get(0).getContent() : null;
        var lastMsgAt = lastMsgPage.hasContent() ? lastMsgPage.getContent().get(0).getCreatedAt() : conv.getCreatedAt();

        long unread = messageRepository.countByConversationIdAndReadFalseAndSenderIdNot(conv.getId(), currentUserId);

        return ConversationResponse.builder()
                .id(conv.getId())
                .listingId(conv.getListing() != null ? conv.getListing().getId() : null)
                .listingTitle(conv.getListing() != null ? conv.getListing().getTitle() : null)
                .listingCoverImage(null) // Could be optimized later
                .participants(participants)
                .lastMessage(lastMsg)
                .lastMessageAt(lastMsgAt)
                .unreadCount(unread)
                .build();
    }

    private MessageResponse toMessageResponse(Message msg) {
        return MessageResponse.builder()
                .id(msg.getId())
                .conversationId(msg.getConversation().getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getFullName())
                .senderAvatar(msg.getSender().getAvatarUrl())
                .content(msg.getContent())
                .imageUrl(msg.getImageUrl())
                .read(msg.isRead())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
