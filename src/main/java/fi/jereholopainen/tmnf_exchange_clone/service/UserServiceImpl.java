package fi.jereholopainen.tmnf_exchange_clone.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fi.jereholopainen.tmnf_exchange_clone.exception.UserAlreadyExistsException;
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
    public Optional<AppUser> findByUsername(String username) {
        // get user from the database by username
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<AppUser> findByTmnfLogin(String tmnfLogin) {
        // get user from the database by tmnfLogin
        return userRepository.findByTmnfLogin(tmnfLogin);
    }

    @Transactional
    @Override
    public void updateTmnfLogin(String username, String tmnfLogin){
        Optional<AppUser> user = userRepository.findByUsername(username);
        if(user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // check if tmnflogin is already in use by another user
        Optional<AppUser> existingUserWithTmnfLogin = userRepository.findByTmnfLogin(tmnfLogin);
        if (existingUserWithTmnfLogin.isPresent() && !existingUserWithTmnfLogin.get().getUsername().equals(username)) {
            throw new RuntimeException("TMNF login is already in use by another user");
        }
        
        AppUser existingUser = user.get();
        existingUser.setTmnfLogin(tmnfLogin);
        userRepository.save(existingUser);
    }

}
