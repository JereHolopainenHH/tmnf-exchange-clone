package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.service.UserService;
import fi.jereholopainen.tmnf_exchange_clone.web.dto.RegistrationRequest;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "message", required = false) String message, Model model) {
        if(message != null) {
            model.addAttribute("message", message);
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid RegistrationRequest registrationRequest, RedirectAttributes redirectAttributes, Model model) {
        if(!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())){
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        try{
            AppUser newUser = new AppUser();
            newUser.setUsername(registrationRequest.getUsername()); // set username
            newUser.setPasswordHash(registrationRequest.getPassword()); // set password
            userService.save(newUser); // save the user to the database
            redirectAttributes.addFlashAttribute("message", "User: " + newUser.getUsername() + " created successfully");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}