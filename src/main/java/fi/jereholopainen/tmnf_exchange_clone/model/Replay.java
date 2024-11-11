package fi.jereholopainen.tmnf_exchange_clone.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Replay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Replay author is required")
    private String author;

    @NotBlank(message = "Replay filepath is required")
    @Column(unique = true)
    private String filePath;

    @NotBlank(message = "Track uid that the replay is for is required")
    @Column(unique = true)
    private String uid;

    @ManyToOne
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    @NotNull(message = "Replay needs to be associated with a track")
    private Track track;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull(message = "Replay needs to be associated with a user")
    private AppUser user;

    // Constructors
    public Replay() {
    }

    public Replay(String author, String filePath, String uid, Track track, AppUser user) {
        this.author = author;
        this.filePath = filePath;
        this.uid = uid;
        this.track = track;
        this.user = user;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    

}
