package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import fi.jereholopainen.tmnf_exchange_clone.entity.AppUser;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByTmnfLogin(String tmnfLogin);
}
