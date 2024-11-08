package fi.jereholopainen.tmnf_exchange_clone.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
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
import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTmnfLoginException;
import fi.jereholopainen.tmnf_exchange_clone.exception.UserNotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;
import fi.jereholopainen.tmnf_exchange_clone.repository.AppUserRepository;
import fi.jereholopainen.tmnf_exchange_clone.repository.TrackRepository;

import static fi.jereholopainen.tmnf_exchange_clone.util.FileUtils.*;

@Service
public class TrackServiceImpl implements TrackService {
    private static final Logger logger = LoggerFactory.getLogger(TrackServiceImpl.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final TrackRepository trackRepository;
    private final UserService userService;

    public TrackServiceImpl(TrackRepository trackRepository, UserService userService) {
        this.trackRepository = trackRepository;
        this.userService = userService;
    }

    public Track saveFile(MultipartFile file) throws IOException {

        logger.info("Starting to save file: {}", file.getOriginalFilename());

        // Validate file type
        if (!isValidFileType(file, ".gbx")) {
            throw new InvalidFileTypeException("Invalid file type. Only .gbx files are allowed.");
        }
        // get currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser user = new AppUser();
        try {
            user = userService.findByUsername(username);
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            throw new UserNotFoundException("User not found with the username: " + username);
        }

        logger.info("Authenticated user: {}", user.getUsername());

        // Check if the user's tmnflogin is set
        if (user.getTmnfLogin() == null || user.getTmnfLogin().isEmpty()) {
            throw new InvalidTmnfLoginException("Your TMNF login is not set. Please update your profile.");
        }

        String uid = extractUidFromFile(file);
        String author = extractAuthorFromFile(file);
        logger.info("Extracted UID: {}", uid);
        logger.info("Extracted author: {}", author);

        // Check if the user's tmnflogin matches the author
        if (!author.equals(user.getTmnfLogin())) {
            throw new InvalidTrackAuthorException("The author of the track does not match your TMNF login.");
        }

        // check if a track with the same UID already exists
        if (trackRepository.findByUid(uid).isPresent()) {
            throw new InvalidTrackUIDException("Track with the same UID already exists.");
        }

        // Simulate the file path
        String fileName = file.getOriginalFilename();
        String simulatedFilePath = Paths.get(uploadDir, fileName).toString();
        logger.info("Simulated file path: {}", simulatedFilePath);

        // Save file path to database
        Track track = new Track();
        track.setName(fileName);
        track.setFilePath(simulatedFilePath);
        track.setUid(uid);
        track.setAuthor(author);
        track.setUser(user);

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
}
