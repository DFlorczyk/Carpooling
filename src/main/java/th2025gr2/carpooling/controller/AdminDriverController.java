package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.model.UserDriverTickets;
import th2025gr2.carpooling.repository.UserDriverTicketsRepository;
import th2025gr2.carpooling.repository.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/drivers")
@RequiredArgsConstructor
public class AdminDriverController {

    private final UserRepository userRepository;
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
        User user = userRepository.findById(userId).orElseThrow();
        UserDriverTickets ticket = ticketsRepository.findByUser(user).orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(ticket.getDriverLicense());
    }

    @PostMapping("/approve/{userId}")
    public String approve(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setDriver(true);
        userRepository.save(user);
        ticketsRepository.findByUser(user).ifPresent(ticketsRepository::delete);
        return "redirect:/admin/drivers";
    }

    @PostMapping("/reject/{userId}")
    public String reject(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        ticketsRepository.findByUser(user).ifPresent(ticketsRepository::delete);
        return "redirect:/admin/drivers";
    }
}