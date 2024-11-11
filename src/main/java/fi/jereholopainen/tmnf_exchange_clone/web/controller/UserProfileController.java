package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fi.jereholopainen.tmnf_exchange_clone.exception.NotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.exception.TmnfLoginTakenException;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute AppUser user, RedirectAttributes redirectAttributes, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            AppUser userToUpdate = userService.findByUsername(username);

            AppUser tmnfLoginTaken = userService.findByTmnfLogin(user.getTmnfLogin());
            if(tmnfLoginTaken != null && !tmnfLoginTaken.getUsername().equals(username)) {
                throw new TmnfLoginTakenException("TMNF login already taken");
            }

            userToUpdate.setTmnfLogin(user.getTmnfLogin());
            userService.updateTmnfLogin(userToUpdate.getUsername(), userToUpdate.getTmnfLogin());
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (TmnfLoginTakenException e) {
            model.addAttribute("error", e.getMessage());
            return "profile";
        }

        return "redirect:/trackupload";
    }

}
