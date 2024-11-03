package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;

public interface TrackRepository extends CrudRepository<Track, Long> {
    Optional<Track> findByUid(String uid);
    List<Track> findByUser(AppUser user);
}
