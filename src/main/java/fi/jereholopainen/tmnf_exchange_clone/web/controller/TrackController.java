package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidFileTypeException;
import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTrackAuthorException;
import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTrackUIDException;
import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTmnfLoginException;
import fi.jereholopainen.tmnf_exchange_clone.exception.UserNotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;
import fi.jereholopainen.tmnf_exchange_clone.service.TrackService;
import fi.jereholopainen.tmnf_exchange_clone.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tracks")
public class TrackController {

    private final TrackService trackService;
    private final UserService userService;

    public TrackController(TrackService trackService, UserService userService) {
        this.trackService = trackService;
        this.userService = userService;
    }

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request, Model model,
            RedirectAttributes redirectAttributes, HttpServletResponse response) {
        try {
            trackService.saveFile(file);
            model.addAttribute("success", "File uploaded successfully: " + file.getOriginalFilename());
        } catch (InvalidFileTypeException | InvalidTrackAuthorException | InvalidTrackUIDException e) {
            model.addAttribute("error", e.getMessage());
        } catch (InvalidTmnfLoginException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile";
        } catch (UserNotFoundException e) {
            // Log out the user and redirect to login page
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        } catch (IOException e) {
            model.addAttribute("error", "Could not save file: " + file.getOriginalFilename());
        }
        return "upload";
    }

    @GetMapping("/user/{id}")
    public String showUserTracks(@PathVariable("id") Long userId, Model model) {
        try {
            AppUser user = userService.findByUserId(userId);
            model.addAttribute("tracks", trackService.getUserTracks(user));
            model.addAttribute("username", user.getUsername());
        } catch (UserNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "tracks";
    }

    @GetMapping
    public String showAllTracks(Model model) {
        List<Track> tracks = trackService.getAllTracks();
        model.addAttribute("tracks", tracks);
        return "tracks";
    }
}
