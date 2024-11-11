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

@Component
public class DataInitializer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final RoleRepository roleRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    public DataInitializer(RoleRepository roleRepository, AppUserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        logger.info("Initializing data");

        // Check if admin role already exists
        if (roleRepository.findByName(ADMIN_ROLE).isEmpty()) {
            Role adminRole = roleRepository.save(new Role(ADMIN_ROLE));
            logger.info("Created role: {}", adminRole.getName());
        }

        // Check if user role already exists
        if (roleRepository.findByName(USER_ROLE).isEmpty()) {
            Role userRole = roleRepository.save(new Role(USER_ROLE));
            logger.info("Created role: {}", userRole.getName());
        }

        // Check if admin user already exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            String adminPassword = System.getenv("ADMIN_PASSWORD"); // Directly get the environment variable

            if (adminPassword != null) {
                Role adminRole = roleRepository.findByName(ADMIN_ROLE).orElseThrow();
                Role userRole = roleRepository.findByName(USER_ROLE).orElseThrow();

                AppUser admin = new AppUser("admin", passwordEncoder.encode(adminPassword), null,
                        Set.of(adminRole, userRole));
                userRepository.save(admin);
                logger.info("Created user: {}", admin.getUsername());
            } else {
                logger.warn("Admin password not set. Skipping admin user creation.");
            }
        }else {
            logger.info("Admin user already exists, skipping creation.");
        }
        logger.info("Data initialization complete");
    }

}
