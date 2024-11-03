package fi.jereholopainen.tmnf_exchange_clone.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import fi.jereholopainen.tmnf_exchange_clone.model.Role;

@Service
public interface RoleService {
    Optional<Role> findByName(String name);
}
