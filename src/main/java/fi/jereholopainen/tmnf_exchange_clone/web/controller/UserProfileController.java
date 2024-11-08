package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import java.lang.ProcessBuilder.Redirect;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fi.jereholopainen.tmnf_exchange_clone.exception.UserNotFoundException;
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
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            AppUser user = userService.findByUsername(username);
            model.addAttribute("user", user);
        } catch (UserNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute AppUser user, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            AppUser userToUpdate = userService.findByUsername(username);
            userToUpdate.setTmnfLogin(user.getTmnfLogin());
            userService.updateTmnfLogin(userToUpdate.getUsername(), userToUpdate.getTmnfLogin());
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/tracks/upload";
    }

}
