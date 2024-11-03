package fi.jereholopainen.tmnf_exchange_clone.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.jereholopainen.tmnf_exchange_clone.entity.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.entity.Comment;
import fi.jereholopainen.tmnf_exchange_clone.entity.Track;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByTrack(Track track);
    List<Comment> findByUser(AppUser user);
}
