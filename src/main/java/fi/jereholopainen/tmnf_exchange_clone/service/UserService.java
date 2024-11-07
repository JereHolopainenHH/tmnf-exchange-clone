package fi.jereholopainen.tmnf_exchange_clone.service;

import java.util.Optional;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;

public interface UserService {
    void save(AppUser user);
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByTmnfLogin(String tmnfLogin);
    void updateTmnfLogin(String username, String tmnfLogin);
}
