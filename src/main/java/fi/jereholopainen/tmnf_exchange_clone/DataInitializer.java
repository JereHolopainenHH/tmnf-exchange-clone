package fi.jereholopainen.tmnf_exchange_clone;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Role;
import fi.jereholopainen.tmnf_exchange_clone.repository.AppUserRepository;
import fi.jereholopainen.tmnf_exchange_clone.repository.RoleRepository;
import io.github.cdimascio.dotenv.Dotenv;

@Component
public class DataInitializer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final RoleRepository roleRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing data");

        Role adminRole = roleRepository.save(new Role("ADMIN"));
        logger.info("Created role: {}", adminRole.getName());

        Role userRole = roleRepository.save(new Role("USER"));
        logger.info("Created role: {}", userRole.getName());
        
        Dotenv dotenv = Dotenv.load();
        String adminPassword = dotenv.get("ADMIN_PASSWORD");

        AppUser admin = new AppUser("admin", passwordEncoder.encode(adminPassword), null, Set.of(adminRole, userRole));
        logger.info("Created user: {}", admin.getUsername());

        userRepository.save(admin);
        logger.info("Data initialized");

        AppUser user = new AppUser("user", passwordEncoder.encode("salasana"), null, Set.of(userRole));
        logger.info("Created user: {}", user.getUsername());

        userRepository.save(user);
        
    }

}
