package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import th2025gr2.carpooling.model.RideParticipant;
import th2025gr2.carpooling.model.UserReview;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import th2025gr2.carpooling.service.ReviewService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserProfileRepository userProfileRepository;

    @GetMapping("/review/ride/{rideId}")
    public String reviewPage(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetailsWithId userDetails,
            Model model
    ) {
        var currentProfile = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        List<RideParticipant> participants;
        String errorMessage = null;
        try {
            participants = reviewService.getParticipantsToRate(rideId, currentProfile.getId());
        } catch (IllegalStateException e) {
            participants = List.of();
            errorMessage = e.getMessage();
        }

        List<UserReview> submittedReviews = errorMessage == null
                ? reviewService.getSubmittedReviewsForRide(rideId, userDetails.getId())
                : List.of();

        model.addAttribute("pageTitle", "Oceń uczestników");
        model.addAttribute("view", "review");
        model.addAttribute("rideId", rideId);
        model.addAttribute("userId", currentProfile.getId());
        model.addAttribute("participants", participants);
        model.addAttribute("submittedReviews", submittedReviews);
        model.addAttribute("errorMessage", errorMessage);
        return "layout";
    }

    @PostMapping("/review/ride/{rideId}/user/{targetProfileId}")
    public String submitReview(
            @PathVariable Long rideId,
            @PathVariable Long targetProfileId,
            @RequestParam int stars,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        reviewService.submitReview(rideId, targetProfileId, stars, comment, userDetails.getId());
        return "redirect:/review/ride/" + rideId;
    }
}
