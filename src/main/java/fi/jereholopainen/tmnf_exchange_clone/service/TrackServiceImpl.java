package fi.jereholopainen.tmnf_exchange_clone.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidFileTypeException;
import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTrackAuthorException;
import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTrackUIDException;
import fi.jereholopainen.tmnf_exchange_clone.exception.TmnfLoginNotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.exception.UserNotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Comment;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;
import fi.jereholopainen.tmnf_exchange_clone.repository.CommentRepository;
import fi.jereholopainen.tmnf_exchange_clone.repository.TrackRepository;
import fi.jereholopainen.tmnf_exchange_clone.web.dto.TrackUploadRequest;
import jakarta.transaction.Transactional;

import static fi.jereholopainen.tmnf_exchange_clone.util.FileUtils.*;

@Service
public class TrackServiceImpl implements TrackService {
    private static final Logger logger = LoggerFactory.getLogger(TrackServiceImpl.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final TrackRepository trackRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public TrackServiceImpl(TrackRepository trackRepository, UserService userService, CommentRepository commentRepository) {
        this.trackRepository = trackRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Track saveTrack(TrackUploadRequest trackUploadRequest) throws IOException {

        MultipartFile file = trackUploadRequest.getFile();
        String difficulty = trackUploadRequest.getDifficulty();
        String tag = trackUploadRequest.getTag();

        logger.info("Starting to save file: {}", file.getOriginalFilename());

        // Validate file type
        if (!isValidTrackFile(file)) {
            throw new InvalidFileTypeException("Invalid file type. Only .Challenge.Gbx files are allowed.");
        }
        // get currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        AppUser user = userService.findByUsername(username);
        if (user == null) {
            logger.error("User not found: {}", username);
            throw new UserNotFoundException("User not found with the username: " + username);
        }

        logger.info("Authenticated user: {}", user.getUsername());
        logger.info("Authenticated user's TMNF login: {}", user.getTmnfLogin());

        Pattern uidPattern = Pattern.compile("uid=\"([^\"]*)\"");
        Pattern authorPattern = Pattern.compile("author=\"([^\"]*)\"");
        Pattern typePattern = Pattern.compile("<desc[^>]*\\stype=\"([^\"]*)\"");

        String uid = extractFromFile(file, uidPattern);
        String author = extractFromFile(file, authorPattern);
        String type = extractFromFile(file, typePattern);
        logger.info("Extracted UID: {}", uid);
        logger.info("Extracted author: {}", author);
        logger.info("Extracted type: {}", type);

        // Check if the user's tmnflogin is set
        if (!isAdmin(user) && user.getTmnfLogin() == null) {
            throw new TmnfLoginNotFoundException("Your TMNF login is not set. Please update your profile.");
        }

        // Check if the user's tmnflogin matches the author
        if (!isAdmin(user) && !author.equals(user.getTmnfLogin())) {
            throw new InvalidTrackAuthorException("The author of the track does not match your TMNF login.");
        }

        // check if a track with the same UID already exists
        if (trackRepository.findByUid(uid).isPresent()) {
            throw new InvalidTrackUIDException("Track with the same UID already exists.");
        }

        // Simulate the file path
        String fileName = file.getOriginalFilename();
        String basename = getBasenameWithoutExtension(fileName);
        String simulatedFilePath = Paths.get(uploadDir, fileName).toString();
        logger.info("Simulated file path: {}", simulatedFilePath);

        // Save file path to database
        Track track = new Track();
        track.setName(basename);
        track.setFilePath(simulatedFilePath);
        track.setUid(uid);
        track.setAuthor(author);
        track.setUser(user);
        track.setTag(tag);
        track.setDifficulty(difficulty);
        track.setType(type);

        Track savedTrack = trackRepository.save(track);
        logger.info("Saved track to database: {}", savedTrack);

        return savedTrack;
    }

    public List<Track> getUserTracks(AppUser user) {
        return trackRepository.findByUser(user);
    }

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    public Track getTrackById(Long id) {
        Optional<Track> track = trackRepository.findById(id);
        return track.get();
    }

    public List<Track> getTracksByAuthor(String author) {
        return trackRepository.findByAuthor(author);
    }

    private boolean isAdmin(AppUser user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }

    public void postComment(AppUser user, Long trackId, String commentText){
        Track track = getTrackById(trackId);
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setCommentText(commentText);
        track.addComment(comment);
        commentRepository.save(comment);
    }
}
