package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;

public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByUid(String uid);
    List<Track> findByUser(AppUser user);
    List<Track> findByAuthor(String author);
}
