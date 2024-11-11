package fi.jereholopainen.tmnf_exchange_clone.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "TMNF login is required")
    @Size(min = 5, max = 20, message = "TMNF login must be between 5 and 20 characters")
    private String tmnfLogin;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String username, String tmnfLogin) {
        this.username = username;
        this.tmnfLogin = tmnfLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTmnfLogin() {
        return tmnfLogin;
    }

    public void setTmnfLogin(String tmnfLogin) {
        this.tmnfLogin = tmnfLogin;
    }

}
