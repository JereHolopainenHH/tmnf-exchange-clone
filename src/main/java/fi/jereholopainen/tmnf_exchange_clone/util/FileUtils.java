package fi.jereholopainen.tmnf_exchange_clone.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
        // Private constructor to prevent instantiation
    }

    public static boolean isValidFileType(MultipartFile file, String extension) {
        String fileName = file.getOriginalFilename();
        logger.info("File name: {}", fileName);
        return fileName != null && fileName.toLowerCase().endsWith(extension);
    }

    public static boolean isValidTrackFile(MultipartFile file) {
        return isValidFileType(file, ".challenge.gbx");
    }

    public static boolean isValidReplayFile(MultipartFile file) {
        return isValidFileType(file, ".replay.gbx");
    }

    public static String extractFromFile(MultipartFile file, Pattern pattern) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        throw new IOException("Pattern " + pattern.toString() + " not found in the file");
    }

    public static String getBasenameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        String challengeExt = ".challenge.gbx";
        String replayExt = ".replay.gbx";
        if (fileName.toLowerCase().endsWith(challengeExt)) {
            return fileName.substring(0, fileName.length() - challengeExt.length());
        }
        if (fileName.toLowerCase().endsWith(replayExt)) {
            return fileName.substring(0, fileName.length() - replayExt.length());
        }
        return fileName; // No extension found
    }

    // Check if user's TMNF login exists in the file
    public static boolean checkTmnfLogin(String content, String tmnfLogin) {
        return content.contains(tmnfLogin);
    }

    // Extract best time in milliseconds
    public static int extractBestTimeInMilliseconds(String content) {
        Pattern pattern = Pattern.compile("<times best=\"(\\d+)\"");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1; // Return -1 if the pattern is not found
    }
}

/*
 * extract challenge uid
 * 
 * check if tmnflogin found in the file
 * 
 * extract times best in ms
 */