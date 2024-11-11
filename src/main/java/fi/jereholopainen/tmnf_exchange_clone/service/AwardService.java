package fi.jereholopainen.tmnf_exchange_clone.service;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;

public interface AwardService {
    void giveAward(AppUser user, Long trackId);
}
