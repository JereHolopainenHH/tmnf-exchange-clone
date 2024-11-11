package fi.jereholopainen.tmnf_exchange_clone.web.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public class ReplayRequest {

    @NotNull
    private MultipartFile file;

    public ReplayRequest() {
    }

    public ReplayRequest(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
