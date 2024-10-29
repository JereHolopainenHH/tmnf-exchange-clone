package fi.jereholopainen.tmnf_exchange_clone.entity;

import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Track {

   // Track(id, name, description, uid, filePath, User<userId>, List<Comment>, List<Replay>)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @NotBlank(message = "Track name is required")
    private String name; // Track name

    private String description; // Track description

    @NotBlank(message = "Track uid is required")
    private String uid; // Track uid

    @NotBlank(message = "Track filepath is required")
    private String filePath; // Track filepath

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull(message = "Track needs to be associated with a user")
    private AppUser user; // User who uploaded the track

    @OneToMany(mappedBy = "track")
    private List<Comment> comments; // Comments on the track

    @OneToMany(mappedBy = "track")
    private List<Replay> replays; // Replays for the track

    // Constructors
    public Track() {
    }

    public Track(String name, String description, String uid, String filePath, AppUser user) {
        this.name = name;
        this.description = description;
        this.uid = uid;
        this.filePath = filePath;
        this.user = user;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Replay> getReplays() {
        return replays;
    }

    public void setReplays(List<Replay> replays) {
        this.replays = replays;
    }

    
}
