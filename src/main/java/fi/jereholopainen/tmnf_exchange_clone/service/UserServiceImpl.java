package fi.jereholopainen.tmnf_exchange_clone.service;

import java.util.Optional;
import java.util.Set;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fi.jereholopainen.tmnf_exchange_clone.exception.MissingInputException;
import fi.jereholopainen.tmnf_exchange_clone.exception.NotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.exception.TmnfLoginTakenException;
import fi.jereholopainen.tmnf_exchange_clone.exception.UsernameTakenException;
import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Role;
import fi.jereholopainen.tmnf_exchange_clone.repository.AppUserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(AppUserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Transactional
    @Override
    public void save(AppUser user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        Optional<Role> userRole = roleService.findByName("USER");
        user.setRoles(Set.of(userRole.get()));
        userRepository.save(user);
    }

    @Override
    public AppUser findByUsername(String username) {
        Optional<AppUser> user = userRepository.findByUsername(username);
        // get user from the database by username
        return user.get();
    }

    @Override
    public AppUser findByTmnfLogin(String tmnfLogin) {
        logger.info("Finding user by tmnfLogin: {}", tmnfLogin);
        Optional<AppUser> user = userRepository.findByTmnfLogin(tmnfLogin);
        // get user from the database by tmnfLogin
        return user.get();
    }

    @Transactional
    @Override
    public void updateTmnfLogin(String username, String tmnfLogin) {
        boolean tmnfLoginTaken = isTmnfLoginTaken(tmnfLogin);
        if(tmnfLoginTaken) {
            throw new TmnfLoginTakenException("Tmnf login " + tmnfLogin + " is already taken.");
        }
        AppUser user = userRepository.findByUsername(username).get();
        user.setTmnfLogin(tmnfLogin);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUsername(String username, String newUsername) {
        boolean usernameTaken = isUsernameTaken(newUsername);
        if(usernameTaken) {
            throw new UsernameTakenException("Username " + newUsername + " is already taken.");
        }
        AppUser user = userRepository.findByUsername(username).get();
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updatePassword(String username, String newPassword) {
        AppUser user = userRepository.findByUsername(username).get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public AppUser findByUserId(Long userId) {
        Optional<AppUser> user = userRepository.findById(userId);
        // get user from the database by userId
        return user.get();
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean isTmnfLoginTaken(String tmnfLogin) {
        return userRepository.findByTmnfLogin(tmnfLogin).isPresent();
    }

    @Override
    public boolean checkPassword(String username, String password) {
        AppUser user = userRepository.findByUsername(username).get();
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

}
