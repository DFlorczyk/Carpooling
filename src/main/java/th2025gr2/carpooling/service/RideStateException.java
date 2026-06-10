package th2025gr2.carpooling.service;

import org.springframework.http.HttpStatus;

/**
 * Thrown by {@link RideRestService} state-transition methods to signal
 * that the request should produce a specific HTTP status (403/404/409)
 * with a {"error": "..."} body. Mapped in the controller layer.
 */
public class RideStateException extends RuntimeException {
    private final HttpStatus status;

    public RideStateException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
