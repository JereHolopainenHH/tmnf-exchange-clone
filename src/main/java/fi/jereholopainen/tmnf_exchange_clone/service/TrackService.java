package fi.jereholopainen.tmnf_exchange_clone.service;

import java.io.IOException;
import java.util.List;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;
import fi.jereholopainen.tmnf_exchange_clone.web.dto.TrackUploadRequest;

public interface TrackService {
    Track saveTrack(TrackUploadRequest trackUploadRequest) throws IOException;
    List<Track> getAllTracks();
    List<Track> getUserTracks(AppUser user);
    Track getTrackById(Long id);
    List<Track> getTracksByAuthor(String username);
}
