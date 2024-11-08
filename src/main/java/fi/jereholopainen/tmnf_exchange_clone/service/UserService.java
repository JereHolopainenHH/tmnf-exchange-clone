package fi.jereholopainen.tmnf_exchange_clone.service;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;

public interface UserService {
    void save(AppUser user);
    AppUser findByUsername(String username);
    AppUser findByTmnfLogin(String tmnfLogin);
    AppUser findByUserId(Long userId);
    void updateTmnfLogin(String username, String tmnfLogin);
}
