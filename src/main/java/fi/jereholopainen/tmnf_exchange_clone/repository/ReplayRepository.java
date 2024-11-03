package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.jereholopainen.tmnf_exchange_clone.entity.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.entity.Track;
import fi.jereholopainen.tmnf_exchange_clone.entity.Replay;

public interface ReplayRepository extends CrudRepository<Replay, Long>{
    List<Replay> findByTrack(Track track);
    List<Replay> findByUser(AppUser user);
}