package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Replay;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;

public interface ReplayRepository extends JpaRepository<Replay, Long>{
    List<Replay> findByTrack(Track track);
    List<Replay> findByUser(AppUser user);
}