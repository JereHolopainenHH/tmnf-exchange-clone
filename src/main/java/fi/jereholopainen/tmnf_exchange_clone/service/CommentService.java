package fi.jereholopainen.tmnf_exchange_clone.service;

import fi.jereholopainen.tmnf_exchange_clone.model.Comment;

public interface CommentService {
    Comment getCommentById(Long commentId);
    void deleteComment(Long id);
}
