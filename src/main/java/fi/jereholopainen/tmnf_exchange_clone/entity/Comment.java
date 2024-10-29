package fi.jereholopainen.tmnf_exchange_clone.entity;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @NotBlank(message = "Comment text required")
    @Size(max = 500, message = "Comment text must be at most 500 characters long")
    private String commentText; // Comment text

    @NotNull(message = "Comment posted at date required")
    private LocalDateTime postedAt; // Time when the comment was posted

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull(message = "User is required")
    private AppUser user; // User who posted the comment
    
    @ManyToOne
    @JoinColumn(name = "track_id")
    @NotNull(message = "Track is required")
    private Track track; // Track the comment is posted on

    // Constructors
    public Comment() {
    }

    public Comment(String commentText, LocalDateTime postedAt, AppUser user, Track track) {
        this.commentText = commentText;
        this.postedAt = postedAt;
        this.user = user;
        this.track = track;
    }

    @PrePersist
    protected void onCreate() {
        this.postedAt = LocalDateTime.now(); // Set postedAt to current time when the comment is created
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }    
}
