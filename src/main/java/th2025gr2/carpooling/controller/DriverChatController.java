package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th2025gr2.carpooling.model.TicketMessage;
import th2025gr2.carpooling.model.UserDriverTickets;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.TicketMessageRepository;
import th2025gr2.carpooling.repository.UserDriverTicketsRepository;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/driver/chat")
@RequiredArgsConstructor
public class DriverChatController {

    private final UserProfileRepository userProfileRepository;
    private final UserDriverTicketsRepository ticketsRepository;
    private final TicketMessageRepository messageRepository;

    @GetMapping
    public String chatView(@AuthenticationPrincipal UserDetailsWithId userDetails, Model model) {
        UserProfile user = userProfileRepository.findByCredentialId(userDetails.getId()).orElseThrow();
        UserDriverTickets ticket = ticketsRepository.findByUser(user).orElse(null);

        if (ticket == null) return "redirect:/driver/register";

        List<TicketMessage> messages = messageRepository.findByTicketOrderBySentAtAsc(ticket);

        model.addAttribute("pageTitle", "Chat");
        model.addAttribute("view", "driver-chat");
        model.addAttribute("messages", messages);
        return "layout";
    }

    @PostMapping
    public String sendMessage(
            @AuthenticationPrincipal UserDetailsWithId userDetails,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile attachment
    ) {
        UserProfile user = userProfileRepository.findByCredentialId(userDetails.getId()).orElseThrow();
        UserDriverTickets ticket = ticketsRepository.findByUser(user).orElse(null);

        if (ticket == null) return "redirect:/driver/register";

        boolean hasContent = content != null && !content.isBlank();
        boolean hasAttachment = attachment != null && !attachment.isEmpty();

        if (!hasContent && !hasAttachment) return "redirect:/driver/chat";

        TicketMessage message = new TicketMessage();
        message.setTicket(ticket);
        message.setFromAdmin(false);
        message.setContent(hasContent ? content.trim() : null);
        message.setSentAt(LocalDateTime.now());
        message.setReadByAdmin(false);

        if (hasAttachment) {
            String type = attachment.getContentType();
            if (type == null || !type.startsWith("image/") || attachment.getSize() > 5 * 1024 * 1024) {
                return "redirect:/driver/chat";
            }
            try {
                message.setAttachment(attachment.getBytes());
                message.setAttachmentType(type);
            } catch (IOException e) {
                return "redirect:/driver/chat";
            }
        }

        messageRepository.save(message);
        return "redirect:/driver/chat";
    }

    @GetMapping("/attachment/{messageId}")
    public ResponseEntity<byte[]> getAttachment(
            @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        UserProfile user = userProfileRepository.findByCredentialId(userDetails.getId()).orElseThrow();
        UserDriverTickets ticket = ticketsRepository.findByUser(user).orElse(null);
        TicketMessage message = messageRepository.findById(messageId).orElseThrow();

        if (ticket == null || !message.getTicket().getId().equals(ticket.getId())) {
            return ResponseEntity.status(403).build();
        }
        if (message.getAttachment() == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(message.getAttachmentType()))
                .body(message.getAttachment());
    }
}