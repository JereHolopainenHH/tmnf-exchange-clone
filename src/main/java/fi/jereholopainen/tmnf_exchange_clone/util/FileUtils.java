package fi.jereholopainen.tmnf_exchange_clone.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    private FileUtils() {
        // Private constructor to prevent instantiation
    }
    public static boolean isValidFileType(MultipartFile file, String extension) {
        String fileName = file.getOriginalFilename();
        return fileName != null && fileName.toLowerCase().endsWith(extension);
    }

    public static String extractUidFromFile(MultipartFile file) throws IOException {
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

    public static String extractAuthorFromFile(MultipartFile file) throws IOException {
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
