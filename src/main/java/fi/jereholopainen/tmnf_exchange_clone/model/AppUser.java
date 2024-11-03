package fi.jereholopainen.tmnf_exchange_clone.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String username; // Username

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String passwordHash; // Password, min 8 characters, only letters and numbers

    @Column(unique = true)
    private String tmnfLogin; // Trackmania Nations Forever login, used to confirm the author of the track/replay

    @ManyToMany
    @NotNull(message = "Role is required")
    @JoinTable(
        name = "user_role", 
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>(); // User roles

    @OneToMany(mappedBy = "user")
    private List<Track> tracks; // User's tracks

    @OneToMany(mappedBy = "user")
    private List<Replay> replays; // User's replays

    @OneToMany(mappedBy = "user")
    private List<Comment> comments; // User's comments

    // Constructors
    public AppUser() {
    }

    public AppUser(String username, String passwordHash, String tmnfLogin, Set<Role> roles) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.tmnfLogin = tmnfLogin;
        this.roles = roles;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTmnfLogin() {
        return tmnfLogin;
    }

    public void setTmnfLogin(String tmnfLogin) {
        this.tmnfLogin = tmnfLogin;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<Replay> getReplays() {
        return replays;
    }

    public void setReplays(List<Replay> replays) {
        this.replays = replays;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}