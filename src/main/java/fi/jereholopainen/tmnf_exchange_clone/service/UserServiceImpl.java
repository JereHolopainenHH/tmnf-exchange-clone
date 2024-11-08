package fi.jereholopainen.tmnf_exchange_clone.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fi.jereholopainen.tmnf_exchange_clone.exception.InvalidTmnfLoginException;
import fi.jereholopainen.tmnf_exchange_clone.exception.UserAlreadyExistsException;
import fi.jereholopainen.tmnf_exchange_clone.exception.UserNotFoundException;
import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Role;
import fi.jereholopainen.tmnf_exchange_clone.repository.AppUserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

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
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username taken, try something else");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        Optional<Role> userRole = roleService.findByName("USER"); // get the user role and throw an exception if it is not found
        if(userRole.isEmpty()) {
            throw new RuntimeException("User role not found");
        }
        user.setRoles(Set.of(userRole.get()));
        userRepository.save(user);
    }

    @Override
    public AppUser findByUsername(String username) {
        Optional<AppUser> user = userRepository.findByUsername(username);
        if(!user.isPresent()) {
            throw new UserNotFoundException("User not found with username: " + username);
        }
        // get user from the database by username
        return user.get();
    }

    @Override
    public AppUser findByTmnfLogin(String tmnfLogin) {
        Optional<AppUser> user = userRepository.findByTmnfLogin(tmnfLogin);
        if(!user.isPresent()) {
            throw new UserNotFoundException("User not found with tmnfLogin: " + tmnfLogin);
        }
        // get user from the database by tmnfLogin
        return user.get();
    }

    @Transactional
    @Override
    public void updateTmnfLogin(String username, String tmnfLogin){
        Optional<AppUser> user = userRepository.findByUsername(username);
        if(!user.isPresent()) {
            throw new UserNotFoundException("User not found with username: " + username);
        }

        // check if tmnflogin is already in use by another user
        Optional<AppUser> existingUserWithTmnfLogin = userRepository.findByTmnfLogin(tmnfLogin);
        if (existingUserWithTmnfLogin.isPresent() && !existingUserWithTmnfLogin.get().getUsername().equals(username)) {
            throw new InvalidTmnfLoginException("TMNF login is already in use by another user");
        }
        
        AppUser existingUser = user.get();
        existingUser.setTmnfLogin(tmnfLogin);
        userRepository.save(existingUser);
    }

    @Override
    public AppUser findByUserId(Long userId) {
        Optional<AppUser> user = userRepository.findById(userId);
        if(!user.isPresent()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        // get user from the database by userId
        return user.get();
    }

}
