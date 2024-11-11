package fi.jereholopainen.tmnf_exchange_clone.web.dto;

import org.springframework.web.multipart.MultipartFile;

public class TrackUploadRequest {

    private MultipartFile file;
    private String difficulty;
    private String tag;

    public TrackUploadRequest() {
    }

    public TrackUploadRequest(MultipartFile file, String difficulty, String tag) {
        this.file = file;
        this.difficulty = difficulty;
        this.tag = tag;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    

}
