package fi.jereholopainen.tmnf_exchange_clone.web.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteRequest {

    @NotBlank(message = "Username is required")
    private String username;

    public DeleteRequest() {
    }

    public DeleteRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
