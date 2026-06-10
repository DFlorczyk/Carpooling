package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.model.RideRequest;
import th2025gr2.carpooling.repository.RideRequestRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    public static final double SERVICE_FEE = 2.00;

    private final RideRequestRepository rideRequestRepository;

    @Transactional(readOnly = true)
    public RideRequest getRequest(Long requestId) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        // force-init lazy associations needed by the controller
        request.getUser().getCredential().getId();
        request.getRide().getCost();
        return request;
    }

    @Transactional
    public void markAsPaid(Long requestId, Long credentialId) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getUser().getCredential().getId().equals(credentialId)) {
            throw new RuntimeException("Not authorized");
        }
        if (!Boolean.TRUE.equals(request.getIsAccepted())) {
            throw new RuntimeException("Request has not been accepted");
        }
        if (Boolean.TRUE.equals(request.getIsPaid())) {
            throw new RuntimeException("Already paid");
        }

        request.setIsPaid(true);
        rideRequestRepository.save(request);
    }
}
