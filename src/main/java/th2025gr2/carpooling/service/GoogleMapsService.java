package th2025gr2.carpooling.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Serwis do komunikacji z Google Maps API.
 * Obsługuje:
 *  - Geocoding (adres → współrzędne)
 *  - Reverse Geocoding (współrzędne → adres)
 *  - Directions (obliczanie trasy, dystansu, czasu przejazdu)
 */
@Service
public class GoogleMapsService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json";

    // ── Geocoding: adres → współrzędne ──────────────────────────────────

    /**
     * Konwertuje adres tekstowy na współrzędne geograficzne.
     * @return tablica [latitude, longitude] lub null gdy brak wyników
     */
    public double[] geocodeAddress(String address) {
        String url = UriComponentsBuilder.fromHttpUrl(GEOCODE_URL)
                .queryParam("address", address)
                .queryParam("key", apiKey)
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (!"OK".equals(root.path("status").asText())) {
                return null;
            }

            JsonNode location = root.path("results").get(0)
                    .path("geometry").path("location");

            return new double[]{
                    location.path("lat").asDouble(),
                    location.path("lng").asDouble()
            };
        } catch (Exception e) {
            throw new RuntimeException("Błąd geocodingu dla adresu: " + address, e);
        }
    }

    // ── Reverse Geocoding: współrzędne → adres ──────────────────────────

    /**
     * Konwertuje współrzędne geograficzne na czytelny adres.
     */
    public String reverseGeocode(double latitude, double longitude) {
        String url = UriComponentsBuilder.fromHttpUrl(GEOCODE_URL)
                .queryParam("latlng", latitude + "," + longitude)
                .queryParam("key", apiKey)
                .queryParam("language", "pl")
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (!"OK".equals(root.path("status").asText())) {
                return "Nieznana lokalizacja";
            }

            return root.path("results").get(0)
                    .path("formatted_address").asText();
        } catch (Exception e) {
            return "Nieznana lokalizacja";
        }
    }

    // ── Directions: obliczanie trasy ────────────────────────────────────

    /**
     * Pobiera informacje o trasie między dwoma punktami.
     * Zwraca obiekt RouteInfo z dystansem, czasem i zakodowaną polilinią.
     */
    public RouteInfo getDirections(double startLat, double startLng,
                                   double endLat, double endLng) {
        String url = UriComponentsBuilder.fromHttpUrl(DIRECTIONS_URL)
                .queryParam("origin", startLat + "," + startLng)
                .queryParam("destination", endLat + "," + endLng)
                .queryParam("mode", "driving")
                .queryParam("language", "pl")
                .queryParam("key", apiKey)
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (!"OK".equals(root.path("status").asText())) {
                return null;
            }

            JsonNode leg = root.path("routes").get(0).path("legs").get(0);
            String polyline = root.path("routes").get(0)
                    .path("overview_polyline").path("points").asText();

            long distanceMeters = leg.path("distance").path("value").asLong();
            long durationSeconds = leg.path("duration").path("value").asLong();

            return new RouteInfo(
                    distanceMeters / 1000.0,
                    (int) Math.ceil(durationSeconds / 60.0),
                    polyline
            );
        } catch (Exception e) {
            throw new RuntimeException("Błąd pobierania trasy z Google Directions API", e);
        }
    }

    // ── RouteInfo – wynik Directions ────────────────────────────────────

    /**
     * Kontener na dane trasy z Google Directions API.
     */
    public record RouteInfo(
            double distanceKm,
            int durationMinutes,
            String encodedPolyline
    ) {}
}
