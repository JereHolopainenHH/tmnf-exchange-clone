package fi.jereholopainen.tmnf_exchange_clone.service;

import java.util.List;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;

public interface UserService {
    void save(AppUser user);
    AppUser findByUsername(String username);
    AppUser findByTmnfLogin(String tmnfLogin);
    AppUser findByUserId(Long userId);
    void updateTmnfLogin(String username, String tmnfLogin);
    void updateUsername(String oldUsername, String newUsername);
    void updatePassword(String username, String newPassword);
    boolean isUsernameTaken(String username);
    boolean isTmnfLoginTaken(String tmnfLogin);
    boolean checkPassword(String username, String password);
    List<AppUser> getAllUsers();
    void addAdminRole(AppUser user);
}
