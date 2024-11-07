package fi.jereholopainen.tmnf_exchange_clone.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;
import fi.jereholopainen.tmnf_exchange_clone.repository.AppUserRepository;
import fi.jereholopainen.tmnf_exchange_clone.repository.TrackRepository;

@Service
public class TrackService {

    private static final Logger logger = LoggerFactory.getLogger(TrackService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final TrackRepository trackRepository;
    private final AppUserRepository userRepository;

    public TrackService(TrackRepository trackRepository, AppUserRepository userRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
    }

    public Track saveFile(MultipartFile file) throws IOException {

        logger.info("Starting to save file: {}", file.getOriginalFilename());

        // Validate file type
        if (!isValidFileType(file)) {
            throw new IOException("Invalid file type. Only .gbx files are allowed.");
        }

        // get currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        logger.info("Authenticated user: {}", user.getUsername());

        // Check if the user's tmnflogin is set
        if (user.getTmnfLogin() == null || user.getTmnfLogin().isEmpty()) {
            throw new IOException("Your TMNF login is not set. Please update your profile.");
        }

        
        String uid = extractUidFromFile(file);
        String author = extractAuthorFromFile(file);
        logger.info("Extracted UID: {}", uid);
        logger.info("Extracted author: {}", author);

        // Check if the user's tmnflogin matches the author
        if (!author.equals(user.getTmnfLogin())) {
            throw new IOException("The author of the track does not match your TMNF login.");
        }

        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("Created upload directory: {}", uploadPath);
        }

        // Save file to local directory
        Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath);
        logger.info("Saved file to: {}", filePath);

        // Save file path to database
        Track track = new Track();
        track.setName(file.getOriginalFilename());
        track.setFilePath(filePath.toString());
        track.setUid(uid);
        track.setAuthor(author);
        track.setUser(user);

        Track savedTrack = trackRepository.save(track);
        logger.info("Saved track to database: {}", savedTrack);

        return savedTrack;
    }

    private boolean isValidFileType(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName != null && fileName.toLowerCase().endsWith(".gbx");
    }

    private String extractUidFromFile(MultipartFile file) throws IOException {
        // Example implementation to read the file content and extract UID using regex
        // Adjust this method based on the actual file format and UID extraction logic
        Pattern uidPattern = Pattern.compile("uid=\"([^\"]+)\"");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = uidPattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        throw new IOException("UID not found in the file");
    }

    private String extractAuthorFromFile(MultipartFile file) throws IOException {
        // Example implementation to read the file content and extract author using
        // regex
        // Adjust this method based on the actual file format and author extraction
        // logic
        Pattern authorPattern = Pattern.compile("author=\"([^\"]+)\"");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = authorPattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        throw new IOException("Author not found in the file");
    }
}
