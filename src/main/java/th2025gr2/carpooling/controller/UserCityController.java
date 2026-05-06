package th2025gr2.carpooling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.service.UserService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserCityController {

    private final UserService userService;

    public UserCityController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/city")
    public ResponseEntity<?> updateCity(@RequestBody Map<String, String> body, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        UserProfile profile = userService.getCurrentProfile(principal);
        if (profile == null) return ResponseEntity.status(404).build();

        userService.updateCity(profile, body.get("city"));

        return ResponseEntity.ok().build();
    }
}