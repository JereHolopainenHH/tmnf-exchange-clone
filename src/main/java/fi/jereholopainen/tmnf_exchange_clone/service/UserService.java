package fi.jereholopainen.tmnf_exchange_clone.service;

import org.springframework.stereotype.Service;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;

@Service
public interface UserService {
    void save(AppUser user);
    AppUser findByUsername(String username);
}
