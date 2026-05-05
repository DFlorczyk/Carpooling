package th2025gr2.carpooling.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.model.UserDriverTickets;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserDriverTicketsRepository;
import th2025gr2.carpooling.repository.UserProfileRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/drivers")
@RequiredArgsConstructor
public class AdminDriverController {

    private final UserProfileRepository userProfileRepository;
    private final UserDriverTicketsRepository ticketsRepository;

    @GetMapping
    public String list(Model model) {
        List<UserDriverTickets> pending = ticketsRepository.findByUserDriverFalse();
        model.addAttribute("pending", pending);
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
}
