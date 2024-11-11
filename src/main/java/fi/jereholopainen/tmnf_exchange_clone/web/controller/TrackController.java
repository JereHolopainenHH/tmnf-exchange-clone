package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import fi.jereholopainen.tmnf_exchange_clone.model.Comment;
import fi.jereholopainen.tmnf_exchange_clone.model.Replay;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;
import fi.jereholopainen.tmnf_exchange_clone.service.AwardService;
import fi.jereholopainen.tmnf_exchange_clone.service.CommentService;
import fi.jereholopainen.tmnf_exchange_clone.service.ReplayService;
import fi.jereholopainen.tmnf_exchange_clone.service.TrackService;
import fi.jereholopainen.tmnf_exchange_clone.service.UserService;
import fi.jereholopainen.tmnf_exchange_clone.web.dto.ReplayRequest;
import fi.jereholopainen.tmnf_exchange_clone.web.dto.TrackUploadRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tracks")
public class TrackController {

    Logger logger = LoggerFactory.getLogger(TrackController.class);

    private final TrackService trackService;
    private final UserService userService;
    private final AwardService awardService;
    private final CommentService commentService;
    private final ReplayService replayService;

    public TrackController(TrackService trackService, UserService userService, AwardService awardService,
            CommentService commentService, ReplayService replayService) {
        this.trackService = trackService;
        this.userService = userService;
        this.awardService = awardService;
        this.commentService = commentService;
        this.replayService = replayService;
    }

    @GetMapping("/upload")
    public String showUploadForm(TrackUploadRequest trackUploadRequest, Model model) {
        model.addAttribute("trackUploadRequest", trackUploadRequest);
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@ModelAttribute TrackUploadRequest trackUploadRequest, Model model,
            RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        try {
            trackService.saveTrack(trackUploadRequest);
            redirectAttributes.addFlashAttribute("successes",
                    "File uploaded successfully: " + trackUploadRequest.getFile().getOriginalFilename());
            return "redirect:/tracks";
        } catch (InvalidFileTypeException | InvalidTrackAuthorException | InvalidTrackUIDException e) {
            model.addAttribute("errors", e.getMessage());
        } catch (TmnfLoginNotFoundException e) {
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
            return "redirect:/profile";
        } catch (UserNotFoundException e) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
            return "redirect:/login";
        } catch (IOException e) {
            model.addAttribute("errors", "Error uploading file: " + e.getMessage());
        }
        return "upload";
    }

    @GetMapping({ "", "/" })
    public String showAllTracks(@RequestParam(value = "author", required = false) String author, Model model) {
        if (author != null && !author.isEmpty()) {
            List<Track> tracks = trackService.getTracksByAuthor(author);
            model.addAttribute("tracks", tracks);
            model.addAttribute("author", author);
            return "tracks";
        }
        List<Track> tracks = trackService.getAllTracks();
        model.addAttribute("tracks", tracks);
        return "tracks";
    }

    @GetMapping("/{id}")
    public String showTrackById(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes,
            ReplayRequest replayRequest) {
        try {
            logger.info("Track ID: {}", id);
            Track track = trackService.getTrackById(id);
            List<Replay> replays = track.getReplays();
            replays.sort(Comparator.comparingInt(Replay::getTime));
            model.addAttribute("track", track);
            model.addAttribute("replays", replays);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName().equals("anonymousUser")) {
                logger.info("User not authenticated.");
                return "track";
            }

            AppUser user = userService.findByUsername(auth.getName());
            boolean hasAwarded = awardService.hasAwarded(user, track.getId());
            model.addAttribute("replayRequest", replayRequest);
            model.addAttribute("hasAwarded", hasAwarded);
        } catch (TrackNotFoundException e) {
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
            return "redirect:/tracks";
        } catch (NumberFormatException | MethodArgumentTypeMismatchException e) {
            redirectAttributes.addFlashAttribute("errors", "Invalid track ID. ID must be a number.");
            return "redirect:/tracks";
        }
        return "track";
    }

    @PostMapping("/{id}/award")
    public String awardTrack(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = userService.findByUsername(auth.getName());
        try {
            awardService.giveAward(user, id);
            redirectAttributes.addFlashAttribute("successes", "Track awarded successfully.");
        } catch (TrackNotFoundException e) {
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
        }
        return "redirect:/tracks/" + id;
    }

    @PostMapping("/{id}/comments")
    public String postComment(@PathVariable("id") Long id, @RequestParam("commentText") String commentText, Model model,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = userService.findByUsername(auth.getName());
        try {
            trackService.postComment(user, id, commentText);
            redirectAttributes.addFlashAttribute("successes", "Comment posted successfully.");
        } catch (TrackNotFoundException e) {
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
        }

        return "redirect:/tracks/" + id;
    }

    @DeleteMapping("/{trackId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("trackId") Long trackId, @PathVariable("commentId") Long commentId,
            RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = userService.findByUsername(auth.getName());
        logger.info("Deleting comment with ID: {}", commentId);
        try {

            Comment comment = commentService.getCommentById(commentId);
            if (comment.getUser().equals(user)) {
                commentService.deleteComment(commentId);
                redirectAttributes.addFlashAttribute("successes", "Comment deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errors", "You are not authorized to delete this comment.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
        }
        return "redirect:/tracks/" + trackId;
    }

    @PostMapping("/{id}/replays")
    public String uploadReplay(@PathVariable("id") Long trackId, @ModelAttribute ReplayRequest replayRequest, Model model,
            RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        try {
            MultipartFile file = replayRequest.getFile();
            logger.info("Replay file: {}", file.getOriginalFilename());
            replayService.saveReplay(file, trackId);
            logger.info("Replay uploaded successfully.");
            redirectAttributes.addFlashAttribute("successes",
                    "Replay uploaded successfully: " + replayRequest.getFile().getOriginalFilename());
        } catch (InvalidFileTypeException | TrackNotFoundException | InvalidTrackUIDException
                | TmnfLoginNotFoundException e) {
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
        }
        return "redirect:/tracks/" + trackId;
    }
}
