package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fi.jereholopainen.tmnf_exchange_clone.service.TrackService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        try {
            trackService.saveFile(file);
            model.addAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Your TMNF login is not set")) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/profile";
            }
            model.addAttribute("error", "Failed to upload file: " + file.getOriginalFilename() + ". " + e.getMessage());
        }
        return "upload";
    }
}
