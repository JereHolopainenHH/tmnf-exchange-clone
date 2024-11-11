package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidFileTypeException;
import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTrackAuthorException;
import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTrackUIDException;
import fi.jereholopainen.tmnf_exchange_clone.exception.TmnfLoginNotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.exception.TrackNotFoundException;
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
public class TrackController {

    private final TrackService trackService;
    private final UserService userService;

    public TrackController(TrackService trackService, UserService userService) {
        this.trackService = trackService;
        this.userService = userService;
    }

    @GetMapping("/trackupload")
    public String showUploadForm(Model model) {
        model.addAttribute("track", new Track());
        return "upload";
    }

    @PostMapping("/trackupload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model,
            RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        try {
            trackService.saveFile(file);
            model.addAttribute("success", "File uploaded successfully: " + file.getOriginalFilename());
        } catch (InvalidFileTypeException | InvalidTrackAuthorException | InvalidTrackUIDException e) {
            model.addAttribute("error", e.getMessage());
        } catch (TmnfLoginNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile";
        } catch (UserNotFoundException e) {
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

    @GetMapping("/user/{id}/tracks/")
    public String showUserTracks(@PathVariable("id") Long userId, Model model, RedirectAttributes redirectAttributes) {
        try {
            AppUser user = userService.findByUserId(userId);
            model.addAttribute("tracks", trackService.getUserTracks(user));
            model.addAttribute("username", user.getUsername());
        } catch (UserNotFoundException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/tracks";
        }
        return "tracks";
    }

    @GetMapping("/tracks")
    public String showAllTracks(Model model) {
        List<Track> tracks = trackService.getAllTracks();
        model.addAttribute("tracks", tracks);
        return "tracks";
    }

    @GetMapping("/tracks/{id}")
    public String showTrackById(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Track track = trackService.getTrackById(id);
            model.addAttribute("track", track);
        } catch (TrackNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tracks";
        }
        return "track";
    }

    @GetMapping("/tracks?author={author}")
    public String showUserTracks(@RequestParam("author") String author, Model model) {
        List<Track> tracks = trackService.getTracksByAuthor(author);
        model.addAttribute("tracks", tracks);
        return "tracks";
    }
}
