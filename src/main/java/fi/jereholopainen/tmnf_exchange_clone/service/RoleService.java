package fi.jereholopainen.tmnf_exchange_clone.service;

import fi.jereholopainen.tmnf_exchange_clone.model.Role;

public interface RoleService {
    Role findByName(String name);
}
