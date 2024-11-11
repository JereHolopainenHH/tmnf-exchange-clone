package fi.jereholopainen.tmnf_exchange_clone.service;

import org.springframework.stereotype.Service;

import fi.jereholopainen.tmnf_exchange_clone.model.Comment;
import fi.jereholopainen.tmnf_exchange_clone.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
