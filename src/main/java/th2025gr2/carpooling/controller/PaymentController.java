package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import th2025gr2.carpooling.model.RideRequest;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import th2025gr2.carpooling.service.PaymentService;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserProfileRepository userProfileRepository;

    @GetMapping("/payment/request/{requestId}")
    public String paymentPage(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetailsWithId userDetails,
            Model model
    ) {
        RideRequest request = paymentService.getRequest(requestId);

        if (!request.getUser().getCredential().getId().equals(userDetails.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (!Boolean.TRUE.equals(request.getIsAccepted())) {
            return "redirect:/profile";
        }

        double rideCost = request.getRide().getCost();
        double total = rideCost + PaymentService.SERVICE_FEE;

        model.addAttribute("pageTitle", "Payment");
        model.addAttribute("view", "payment");
        model.addAttribute("requestId", requestId);
        model.addAttribute("rideCost", rideCost);
        model.addAttribute("serviceFee", PaymentService.SERVICE_FEE);
        model.addAttribute("total", total);
        model.addAttribute("alreadyPaid", Boolean.TRUE.equals(request.getIsPaid()));
        return "layout";
    }

    @PostMapping("/api/payment/request/{requestId}/process")
    @ResponseBody
    public ResponseEntity<?> processPayment(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        try {
            paymentService.markAsPaid(requestId, userDetails.getId());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
