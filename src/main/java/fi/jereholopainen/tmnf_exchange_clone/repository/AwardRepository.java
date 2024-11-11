package fi.jereholopainen.tmnf_exchange_clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Award;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;

public interface AwardRepository extends JpaRepository<Award, Long> {
    boolean existsByUserAndTrack(AppUser user, Track track);
}
