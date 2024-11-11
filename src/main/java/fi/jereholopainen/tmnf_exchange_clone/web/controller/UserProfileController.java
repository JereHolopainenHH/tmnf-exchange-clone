package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidPasswordException;
import fi.jereholopainen.tmnf_exchange_clone.exception.TmnfLoginTakenException;
import fi.jereholopainen.tmnf_exchange_clone.exception.UsernameTakenException;
import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.service.UserService;
import fi.jereholopainen.tmnf_exchange_clone.web.dto.UpdatePasswordRequest;
import fi.jereholopainen.tmnf_exchange_clone.web.dto.UpdateProfileRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({ "/", "" })
    public String showProfileForm(UpdateProfileRequest updateProfileRequest,
            UpdatePasswordRequest updatePasswordRequest, Model model) {
        model.addAttribute("updateProfileRequest", updateProfileRequest);
        model.addAttribute("updatePasswordRequest", updatePasswordRequest);
        return "profile";
    }

    @PatchMapping("/update-profile")
    public String updateProfile(@Valid @ModelAttribute UpdateProfileRequest updateProfileRequest,
            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = userService.findByUsername(authentication.getName());

        logger.info("Updating profile for user: {}", user.getUsername());

        List<String> errorList = new ArrayList<>();
        List<String> successList = new ArrayList<>();

        if (!updateProfileRequest.getUsername().isEmpty()
                && !updateProfileRequest.getUsername().equals(user.getUsername())) {
            try {
                logger.info("Updating username for user: {} to: {}", user.getUsername(),
                        updateProfileRequest.getUsername());
                userService.updateUsername(user.getUsername(), updateProfileRequest.getUsername());
                successList.add("Username updated successfully");
            } catch (UsernameTakenException e) {
                errorList.add(e.getMessage());
            }
        } else if (!updateProfileRequest.getUsername().isEmpty()
                && updateProfileRequest.getUsername().equals(user.getUsername())) {
            errorList.add("Username is already set to " + user.getUsername());
        }

        if (!updateProfileRequest.getTmnfLogin().isEmpty()
                && !updateProfileRequest.getTmnfLogin().equals(user.getTmnfLogin())) {
            try {
                logger.info("Updating Trackmania login for user: {} to: {}", user.getUsername(),
                        updateProfileRequest.getTmnfLogin());
                userService.updateTmnfLogin(user.getUsername(), updateProfileRequest.getTmnfLogin());
                successList.add("TMNF login updated successfully");
            } catch (TmnfLoginTakenException e) {
                errorList.add(e.getMessage());
            }
        } else if (!updateProfileRequest.getTmnfLogin().isEmpty()
                && updateProfileRequest.getTmnfLogin().equals(user.getTmnfLogin())) {
            errorList.add("Tmnf login is already set to " + user.getTmnfLogin());
        }

        redirectAttributes.addFlashAttribute("errors", errorList);
        redirectAttributes.addFlashAttribute("successes", successList);
        return "redirect:/profile";
    }

    @PatchMapping("/update-password")
    public String updatePassword(@Valid @ModelAttribute UpdatePasswordRequest updatePasswordRequest,
            RedirectAttributes redirectAttributes) {
        List<String> errorList = new ArrayList<>();
        List<String> successList = new ArrayList<>();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AppUser user = userService.findByUsername(authentication.getName());

            logger.info("Updating password for user: {}", user.getUsername());

            String username = user.getUsername();
            String oldPassword = updatePasswordRequest.getOldPassword();
            String newPassword = updatePasswordRequest.getNewPassword();
            String confirmNewPassword = updatePasswordRequest.getConfirmNewPassword();

            if (!oldPassword.isEmpty()) {
                if (userService.checkPassword(username, oldPassword)) {
                    logger.info("Old password is incorrect for user: {}", username);
                    errorList.add("Old password is incorrect");
                    throw new InvalidPasswordException("Old password is incorrect");
                }
            } else {
                errorList.add("Old password cannot be empty");
            }

            if (!newPassword.isEmpty() && newPassword.equals(confirmNewPassword)) {
                logger.info("Updating password for user: {}", username);
                userService.updatePassword(username, newPassword);
                successList.add("Password updated successfully");
            } else {
                errorList.add("New password cannot be empty");
            }
        } catch (InvalidPasswordException e) {
            logger.error("Validation error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errors", errorList);
            return "redirect:/profile";
        }

        redirectAttributes.addFlashAttribute("errors", errorList);
        redirectAttributes.addFlashAttribute("successes", successList);
        return "redirect:/profile";
    }

}
