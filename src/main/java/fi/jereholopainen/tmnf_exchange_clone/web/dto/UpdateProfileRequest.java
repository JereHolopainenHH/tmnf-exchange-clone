package fi.jereholopainen.tmnf_exchange_clone.web.dto;

public class UpdateProfileRequest {

    private String username;
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
