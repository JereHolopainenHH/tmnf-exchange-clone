package fi.jereholopainen.tmnf_exchange_clone.service;

import org.springframework.stereotype.Service;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Award;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;
import fi.jereholopainen.tmnf_exchange_clone.repository.AwardRepository;
import jakarta.transaction.Transactional;
import fi.jereholopainen.tmnf_exchange_clone.exception.TrackNotFoundException;

@Service
public class AwardServiceImpl implements AwardService {

    private final TrackService trackService;
    private final AwardRepository awardRepository;

    public AwardServiceImpl(TrackService trackService, AwardRepository awardRepository) {
        this.trackService = trackService;
        this.awardRepository = awardRepository;
    }

    @Transactional
    @Override
    public void giveAward(AppUser user, Long trackId) {
        Track track = trackService.getTrackById(trackId);
        if(track == null) {
            throw new TrackNotFoundException("Track not found with id: " + trackId);
        }

        // Ensure user is not the author
        if (track.getAuthor().equals(user.getTmnfLogin())) {
            throw new IllegalArgumentException("Authors cannot award their own tracks.");
        }

        if(awardRepository.existsByUserAndTrack(user, track)) {
            throw new IllegalArgumentException("User has already awarded this track.");
        }

        Award award = new Award(user, track);
        awardRepository.save(award);
    }

    @Transactional
    @Override
    public boolean hasAwarded(AppUser user, Long trackId) {
        Track track = trackService.getTrackById(trackId);
        if(track == null) {
            throw new TrackNotFoundException("Track not found with id: " + trackId);
        }

        return awardRepository.existsByUserAndTrack(user, track);
    }

}
