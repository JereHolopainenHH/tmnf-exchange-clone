package fi.jereholopainen.tmnf_exchange_clone.web.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleRequest {

    @NotBlank(message = "Role name is required")
    private String role;

    public RoleRequest() {
    }

    public RoleRequest(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
