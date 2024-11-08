package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByTmnfLogin(String tmnfLogin);
}
