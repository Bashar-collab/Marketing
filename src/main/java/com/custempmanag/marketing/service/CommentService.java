package com.custempmanag.marketing.service;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.controller.OfferingController;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Comment;
import com.custempmanag.marketing.model.Post;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.CommentRepository;
import com.custempmanag.marketing.repository.PostRepository;
import com.custempmanag.marketing.request.CommentRequest;
import com.custempmanag.marketing.response.CommentResponse;
import com.custempmanag.marketing.response.MessageResponse;
import com.google.api.Http;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    @Transactional
    public MessageResponse addCommentToPost(Long postId,
                                            CommentRequest commentRequest,
                                            UserPrinciple currentUser) {

        User user = userService.validateAndGetUserById(currentUser.getId());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment comment = modelMapper.map(commentRequest, Comment.class); // It is not worth it though, LOL
        comment.setUser(user);
        comment.setPost(post);

        comment.setCommentDate(LocalDate.now());
        commentRepository.save(comment);

        return new MessageResponse(HttpStatus.CREATED.toString(), "Comment added successfully", null);

    }

    @Transactional
    public MessageResponse addReplyToComment(Long commentId,
                                             CommentRequest commentRequest
                                            ,UserPrinciple currentUser) {

        User user = userService.validateAndGetUserById(currentUser.getId());
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        Comment reply = new Comment();
        reply.setUser(user);
        reply.setParentComment(parentComment);
        reply.setCommentDate(LocalDate.now());
        reply.setContent(commentRequest.getContent());
        reply.setPost(parentComment.getPost());
//        parentComment.setReplies();
        commentRepository.save(reply);
        return new MessageResponse(HttpStatus.CREATED.toString(), "Reply added successfully", null);

    }

    // NEED TO CHECK IN EVERY REQUEST THE IDENTITY OF THE USER

    @Transactional
    public MessageResponse updateComment(Long commentId,
                                         CommentRequest commentRequest)
    {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        modelMapper.map(commentRequest, comment);
        commentRepository.save(comment);
        return new MessageResponse(HttpStatus.OK.toString(), "Comment updated successfully", null);
    }

    @Transactional
    public MessageResponse deleteComment(Long commentId)
    {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        commentRepository.delete(comment);
        return new MessageResponse(HttpStatus.OK.toString(), "Comment deleted successfully", null);
    }

    public MessageResponse getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        List<Comment> comments = commentRepository.findByPostId(post.getId());
        return new MessageResponse(HttpStatus.OK.toString(), "Comments are retrieved successfully!", comments.stream()
                .filter(comment -> comment.getParentComment() == null) // Only top-level comments
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList()));
    }

    public MessageResponse getCommentDetails(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

//        return new MessageResponse(HttpStatus.OK.toString(), "Comment details are retrieved successfully!",
//                modelMapper.map(comment, CommentResponse.class));
        return new MessageResponse(HttpStatus.OK.toString(), "Comment details are retrieved successfully!",
                mapToCommentResponse(comment));

    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setDate(comment.getCommentDate());

        // Recursively map replies (without parent reference)
        response.setReplies(
                comment.getReplies().stream()
                        .map(this::mapToCommentResponse)
                        .collect(Collectors.toList())
        );

        return response;
    }
}
