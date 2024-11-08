package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Comment;
import fi.jereholopainen.tmnf_exchange_clone.model.Track;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTrack(Track track);
    List<Comment> findByUser(AppUser user);
}
