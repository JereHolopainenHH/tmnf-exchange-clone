package fi.jereholopainen.tmnf_exchange_clone;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Role;
import fi.jereholopainen.tmnf_exchange_clone.repository.RoleRepository;
import fi.jereholopainen.tmnf_exchange_clone.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserService userService;

    public DataInitializer(RoleRepository roleRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.save(new Role("ADMIN"));
        Role userRole = roleRepository.save(new Role("USER"));
        
        Dotenv dotenv = Dotenv.load();
        String adminPassword = dotenv.get("ADMIN_PASSWORD");
        AppUser admin = new AppUser("admin", adminPassword, null, Set.of(adminRole, userRole));

        userService.save(admin);
    }

}
