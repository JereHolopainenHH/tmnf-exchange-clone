package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Role;
import fi.jereholopainen.tmnf_exchange_clone.service.RoleService;
import fi.jereholopainen.tmnf_exchange_clone.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping({ "/", "" })
    public String showAdminPanel(Model model) {
        List<AppUser> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin";
    }

    @PostMapping("/users/make-admin")
    public String makeAdmin(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Making user with id: {} an admin", userId);

            AppUser user = userService.findByUserId(userId);
            userService.addAdminRole(user);
            redirectAttributes.addFlashAttribute("success", "User is now an admin.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", "Failed to update role: " + e.getMessage());
        }

        return "redirect:/admin";
    }
}
