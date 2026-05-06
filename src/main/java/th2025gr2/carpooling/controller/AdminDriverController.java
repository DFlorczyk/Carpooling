package th2025gr2.carpooling.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/drivers")
@RequiredArgsConstructor
public class AdminDriverController {

    private final UserProfileRepository userProfileRepository;
    private final UserDriverTicketsRepository ticketsRepository;
    private final TicketMessageRepository messageRepository;

    @GetMapping
    public String list(Model model) {
        List<UserDriverTickets> pending = ticketsRepository.findByUserDriverFalse();

        java.util.Map<Long, Long> unreadCounts = new java.util.HashMap<>();
        for (UserDriverTickets ticket : pending) {
            unreadCounts.put(ticket.getId(), messageRepository.countByTicketAndFromAdminFalseAndReadByAdminFalse(ticket));
        }

        model.addAttribute("pending", pending);
        model.addAttribute("unreadCounts", unreadCounts);
        model.addAttribute("pageTitle", "Driver Verification");
        model.addAttribute("view", "admin-drivers");
        return "admin-layout";
    }

    @GetMapping("/license/{userId}")
    public ResponseEntity<byte[]> showLicense(@PathVariable Long userId) {
        UserProfile user = userProfileRepository.findById(userId).orElseThrow();
        UserDriverTickets ticket = ticketsRepository.findByUser(user).orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(ticket.getDriverLicense());
    }

    @Transactional
    @PostMapping("/approve/{userId}")
    public String approve(@PathVariable Long userId) {
        UserProfile user = userProfileRepository.findById(userId).orElseThrow();
        user.setDriver(true);
        userProfileRepository.save(user);
        ticketsRepository.deleteByUser(user);
        return "redirect:/admin/drivers";
    }

    @Transactional
    @PostMapping("/reject/{userId}")
    public String reject(@PathVariable Long userId) {
        UserProfile user = userProfileRepository.findById(userId).orElseThrow();
        ticketsRepository.deleteByUser(user);
        return "redirect:/admin/drivers";
    }

    @Transactional
    @GetMapping("/chat/{userId}")
    public String adminChat(@PathVariable Long userId, Model model) {
        UserProfile user = userProfileRepository.findById(userId).orElseThrow();
        UserDriverTickets ticket = ticketsRepository.findByUser(user).orElseThrow();
        List<TicketMessage> messages = messageRepository.findByTicketOrderBySentAtAsc(ticket);

        messages.stream()
                .filter(m -> !m.isFromAdmin() && !m.isReadByAdmin())
                .forEach(m -> m.setReadByAdmin(true));
        messageRepository.saveAll(messages);

        model.addAttribute("pageTitle", "Driver Verification");
        model.addAttribute("view", "admin-driver-chat");
        model.addAttribute("messages", messages);
        model.addAttribute("userId", userId);
        model.addAttribute("userName", user.getName() + " " + user.getSurname());
        return "admin-layout";
    }

    @PostMapping("/chat/{userId}")
    public String adminSendMessage(
            @PathVariable Long userId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile attachment
    ) {
        UserProfile user = userProfileRepository.findById(userId).orElseThrow();
        UserDriverTickets ticket = ticketsRepository.findByUser(user).orElseThrow();

        boolean hasContent = content != null && !content.isBlank();
        boolean hasAttachment = attachment != null && !attachment.isEmpty();

        if (!hasContent && !hasAttachment) return "redirect:/admin/drivers/chat/" + userId;

        TicketMessage message = new TicketMessage();
        message.setTicket(ticket);
        message.setFromAdmin(true);
        message.setContent(hasContent ? content.trim() : null);
        message.setSentAt(LocalDateTime.now());
        message.setReadByAdmin(true);

        if (hasAttachment) {
            String type = attachment.getContentType();
            if (type != null && type.startsWith("image/") && attachment.getSize() <= 5 * 1024 * 1024) {
                try {
                    message.setAttachment(attachment.getBytes());
                    message.setAttachmentType(type);
                } catch (IOException ignored) {
                    return "redirect:/admin/drivers/chat/" + userId;
                }
            }
        }

        messageRepository.save(message);
        return "redirect:/admin/drivers/chat/" + userId;
    }

    @GetMapping("/chat/attachment/{messageId}")
    public ResponseEntity<byte[]> adminGetAttachment(@PathVariable Long messageId) {
        TicketMessage message = messageRepository.findById(messageId).orElseThrow();
        if (message.getAttachment() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(message.getAttachmentType()))
                .body(message.getAttachment());
    }
}
