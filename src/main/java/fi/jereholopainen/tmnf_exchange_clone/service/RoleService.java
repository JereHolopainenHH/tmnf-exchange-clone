package fi.jereholopainen.tmnf_exchange_clone.service;

import java.util.Optional;

import fi.jereholopainen.tmnf_exchange_clone.model.Role;

public interface RoleService {
    Optional<Role> findByName(String name);
}
