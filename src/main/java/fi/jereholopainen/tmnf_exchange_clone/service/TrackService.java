package fi.jereholopainen.tmnf_exchange_clone.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;

public interface TrackService {
    Track saveFile(MultipartFile file) throws IOException;
    List<Track> getAllTracks();
    List<Track> getUserTracks(AppUser user);
    Track getTrackById(Long id);
    List<Track> getTracksByAuthor(String username);
}
