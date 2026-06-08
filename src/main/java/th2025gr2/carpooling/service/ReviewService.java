package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.model.*;
import th2025gr2.carpooling.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final RideRepository rideRepository;
    private final RideParticipantRepository rideParticipantRepository;
    private final UserReviewRepository userReviewRepository;
    private final StarRatingRepository starRatingRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public List<RideParticipant> getParticipantsToRate(Long rideId, Long currentUserProfileId) {
        UserProfile currentUser = userProfileRepository.findById(currentUserProfileId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!"finished".equalsIgnoreCase(ride.getState().getName())) {
            throw new IllegalStateException("You can only rate completed rides");
        }

        return rideParticipantRepository.findByRide_Id(rideId).stream()
                .filter(p -> !p.getUser().getId().equals(currentUserProfileId))
                .filter(p -> !userReviewRepository.existsBySenderAndReceiverAndRide(currentUser, p.getUser(), ride))
                .toList();
    }

    @Transactional
    public void submitReview(Long rideId, Long receiverProfileId, int stars, String comment, Long senderCredentialId) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        UserProfile sender = userProfileRepository.findByCredentialId(senderCredentialId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        UserProfile receiver = userProfileRepository.findById(receiverProfileId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!"finished".equalsIgnoreCase(ride.getState().getName())) {
            throw new IllegalStateException("You can only rate completed rides");
        }

        rideParticipantRepository.findByUser_IdAndRide_Id(sender.getId(), rideId)
                .orElseThrow(() -> new RuntimeException("You are not a participant of this ride"));
        rideParticipantRepository.findByUser_IdAndRide_Id(receiverProfileId, rideId)
                .orElseThrow(() -> new RuntimeException("Target user is not a participant of this ride"));

        if (userReviewRepository.existsBySenderAndReceiverAndRide(sender, receiver, ride)) {
            throw new RuntimeException("You have already reviewed this person for this ride");
        }

        StarRating starRating = starRatingRepository.findByValue((long) stars)
                .orElseThrow(() -> new RuntimeException("Invalid star rating value"));

        UserReview review = new UserReview();
        review.setSender(sender);
        review.setReceiver(receiver);
        review.setRide(ride);
        review.setStarRating(starRating);
        review.setComment(comment != null && !comment.isBlank() ? comment.trim() : null);

        userReviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<UserReview> getSubmittedReviewsForRide(Long rideId, Long senderCredentialId) {
        UserProfile sender = userProfileRepository.findByCredentialId(senderCredentialId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        return userReviewRepository.findBySenderAndRide(sender, ride);
    }

    public List<UserReview> getReceivedReviews(UserProfile user) {
        return userReviewRepository.findByReceiver(user);
    }

    public double getAverageRating(UserProfile user) {
        return userReviewRepository.findAverageRatingByReceiver(user).orElse(0.0);
    }
}
