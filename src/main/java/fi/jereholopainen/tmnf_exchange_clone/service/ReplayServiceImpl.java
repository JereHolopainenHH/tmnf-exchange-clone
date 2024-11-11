package fi.jereholopainen.tmnf_exchange_clone.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidFileTypeException;
import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTrackUIDException;
import fi.jereholopainen.tmnf_exchange_clone.exception.TmnfLoginNotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.exception.TrackNotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Replay;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;
import fi.jereholopainen.tmnf_exchange_clone.repository.ReplayRepository;
import jakarta.transaction.Transactional;

import static fi.jereholopainen.tmnf_exchange_clone.util.FileUtils.*;

@Service
public class ReplayServiceImpl implements ReplayService {

    private static final Logger logger = LoggerFactory.getLogger(ReplayServiceImpl.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final TrackService trackService;
    private final UserService userService;
    private final ReplayRepository replayRepository;

    public ReplayServiceImpl(TrackService trackService, UserService userService, ReplayRepository replayRepository) {
        this.trackService = trackService;
        this.userService = userService;
        this.replayRepository = replayRepository;
    }

    @Transactional
    @Override
    public void saveReplay(MultipartFile file, Long trackId) {

        // Validate file type
        if (!isValidReplayFile(file)) {
            throw new InvalidFileTypeException("Invalid file type. Only .Replay.Gbx files are allowed.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = userService.findByUsername(auth.getName());

        if(user.getTmnfLogin() == null){
            throw new TmnfLoginNotFoundException("User does not have a TMNF login");
        }

        Track track = trackService.getTrackById(trackId);
        if (track == null) {
            throw new TrackNotFoundException("Track with id " + trackId + " not found");
        }

        try {
            // Extract challenge UID
            Pattern uidPattern = Pattern.compile("uid=\"([^\"]*)\"");
            String uid = extractFromFile(file, uidPattern);
            logger.info("Challenge UID: {}", uid);
            if (uid == null || uid.isEmpty()) {
                logger.error("Failed to extract UID from file");
                throw new InvalidTrackUIDException("Failed to extract UID from file");
            } else if (!uid.equals(track.getUid())) {
                logger.error("Invalid UID, expected {} but got {}", track.getUid(), uid);
                throw new InvalidTrackUIDException("Invalid uid, expected " + track.getUid() + " but got " + uid);
            }

            // Extract best time in milliseconds
            Pattern timePattern = Pattern.compile("<times best=\"(\\d+)\"");
            int bestTime = Integer.parseInt(extractFromFile(file, timePattern));

            logger.info("Best time: {}", bestTime);

            // Simulate the file path
            String fileName = file.getOriginalFilename();
            String simulatedFilePath = Paths.get(uploadDir, fileName).toString();

            logger.info("Simulated file path: {}", simulatedFilePath);
            
            // Save the replay
            Replay replay = new Replay();
            replay.setTrack(track);
            replay.setUser(user);
            replay.setFilePath(simulatedFilePath);
            replay.setTime(bestTime);
            replay.setUid(uid);
            replay.setDriver(user.getTmnfLogin());
            replayRepository.save(replay);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process replay file", e);
        }
    }

}
