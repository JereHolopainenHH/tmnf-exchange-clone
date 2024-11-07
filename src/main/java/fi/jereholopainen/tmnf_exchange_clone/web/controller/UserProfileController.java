package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.service.UserService;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showProfileForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<AppUser> user = userService.findByUsername(username);
        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }
        model.addAttribute("user", user.get());
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute AppUser user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<AppUser> optionalUser = userService.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }

        AppUser existingUser = optionalUser.get();
        existingUser.setTmnfLogin(user.getTmnfLogin());
        userService.updateTmnfLogin(existingUser.getUsername(), existingUser.getTmnfLogin());
        return "redirect:/tracks/upload";
    }

}
