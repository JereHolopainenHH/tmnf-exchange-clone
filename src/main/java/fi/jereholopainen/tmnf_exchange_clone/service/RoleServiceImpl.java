package fi.jereholopainen.tmnf_exchange_clone.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import fi.jereholopainen.tmnf_exchange_clone.model.Role;
import fi.jereholopainen.tmnf_exchange_clone.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

}
