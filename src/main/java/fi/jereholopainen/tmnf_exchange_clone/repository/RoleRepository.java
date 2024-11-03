package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import fi.jereholopainen.tmnf_exchange_clone.entity.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
