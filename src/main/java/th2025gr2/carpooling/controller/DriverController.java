package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import th2025gr2.carpooling.model.CarDetail;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.model.UserDriverTickets;
import th2025gr2.carpooling.repository.CarDetailRepository;
import th2025gr2.carpooling.repository.UserDriverTicketsRepository;
import th2025gr2.carpooling.repository.UserRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

import java.io.IOException;

@Controller
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {

    private final UserRepository userRepository;
    private final UserDriverTicketsRepository ticketsRepository;
    private final CarDetailRepository carDetailRepository;

    @GetMapping("/register")
    public String registerForm(@AuthenticationPrincipal UserDetailsWithId userDetails,
                               Model model) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow();

        if (user.isDriver()) return "redirect:/profile";

        if (user.getDriverTickets() != null) {
            model.addAttribute("pageTitle", "Become a Driver");
            model.addAttribute("view", "driver-pending");
            return "layout";
        }

        model.addAttribute("pageTitle", "Become a Driver");
        model.addAttribute("view", "driver-register");
        return "layout";
    }

    @PostMapping("/register")
    public String submitLicense(
            @AuthenticationPrincipal UserDetailsWithId userDetails,
            @RequestParam MultipartFile driverLicense,
            Model model
    ) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow();

        if (user.isDriver()) return "redirect:/profile";
        if (user.getDriverTickets() != null) return "redirect:/driver/register";

        String contentType = driverLicense.getContentType();
        if (driverLicense.isEmpty() || contentType == null || !contentType.startsWith("image/")) {
            model.addAttribute("error", "Only JPG/PNG image files are allowed.");
            model.addAttribute("pageTitle", "Become a Driver");
            model.addAttribute("view", "driver-register");
            return "layout";
        }

        if (driverLicense.getSize() > 5 * 1024 * 1024) {
            model.addAttribute("error", "File too large (max 5MB).");
            model.addAttribute("pageTitle", "Become a Driver");
            model.addAttribute("view", "driver-register");
            return "layout";
        }

        try {
            UserDriverTickets ticket = new UserDriverTickets();
            ticket.setUser(user);
            ticket.setDriverLicense(driverLicense.getBytes());
            ticketsRepository.save(ticket);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to save file. Please try again.");
            model.addAttribute("pageTitle", "Become a Driver");
            model.addAttribute("view", "driver-register");
            return "layout";
        }

        return "redirect:/driver/register";
    }

    @GetMapping("/car")
    public String carForm(@AuthenticationPrincipal UserDetailsWithId userDetails, Model model) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow();

        if (!user.isDriver()) return "redirect:/driver/register";

        CarDetail car = carDetailRepository.findByUser(user).orElse(null);

        model.addAttribute("pageTitle", "Car Details");
        model.addAttribute("view", "driver-car");
        model.addAttribute("car", car);
        return "layout";
    }
    @PostMapping("/car")
    public String submitCar(
            @AuthenticationPrincipal UserDetailsWithId userDetails,
            @RequestParam String carModel,
            @RequestParam Short seatCount,
            @RequestParam Short mileage,
            @RequestParam String color
    ) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow();

        if (!user.isDriver()) return "redirect:/driver/register";

        CarDetail car = carDetailRepository.findByUser(user).orElse(new CarDetail());
        car.setUser(user);
        car.setModel(carModel);
        car.setSeatCount(seatCount);
        car.setMileage(mileage);
        car.setColor(color);
        carDetailRepository.save(car);

        return "redirect:/profile";
    }
}