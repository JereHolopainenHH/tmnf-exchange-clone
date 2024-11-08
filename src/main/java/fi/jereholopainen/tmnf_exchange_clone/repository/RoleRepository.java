package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.jereholopainen.tmnf_exchange_clone.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
