package com.custempmanag.marketing.controller;

/*
    Comment Management APIs:

    Add Comment to Post:
        POST /api/posts/{postId}/comments: Accepts comment text and creates a new comment associated with the specified post by the logged-in user (customer or owner).
    Add Reply to Comment:
        POST /api/comments/{commentId}/replies: Accepts comment text and creates a new comment as a reply to the specified comment by the logged-in user.
    Get Comments for Post:
        GET /api/posts/{postId}/comments: Retrieves a list of comments for a specific post, potentially including nested replies.
    Get Comment Details:
        GET /api/comments/{commentId}: Retrieves details of a specific comment.
    Update Comment:
        PUT /api/comments/{commentId}: Updates the text of a specific comment (requires authentication of the commenter).
    Delete Comment:
        DELETE /api/comments/{commentId}: Deletes a specific comment (requires authentication of the commenter or the post owner).
 */

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.request.CommentRequest;
import com.custempmanag.marketing.response.CommentResponse;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.service.CommentService;
import com.google.api.Http;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER') || hasRole('CUSTOMER')")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<MessageResponse> addCommentToPost(@PathVariable Long postId,
                                              @Valid @RequestBody CommentRequest commentRequest,
                                              @AuthenticationPrincipal UserPrinciple currentUser) {
        logger.info("Adding comment to post {}", postId);
        MessageResponse messageResponse = commentService.addCommentToPost(postId, commentRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<MessageResponse> addReplyToComment(@PathVariable Long commentId,
                                               @Valid @RequestBody CommentRequest commentRequest
                                             , @AuthenticationPrincipal UserPrinciple currentUser) {
        logger.info("Adding reply to comment {}", commentId);
        MessageResponse messageResponse = commentService.addReplyToComment(commentId, commentRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> updateComment(@PathVariable Long commentId,
                                           @Valid @RequestBody CommentRequest commentRequest)
    {
        logger.info("Updating comment {}", commentId);
        MessageResponse messageResponse = commentService.updateComment(commentId, commentRequest);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable Long commentId)
    {
        logger.info("Deleting comment {}", commentId);
        MessageResponse messageResponse = commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/posts/{postId}/comments") // THIS IS NOT WORKING AS EXPECTED
    public ResponseEntity<MessageResponse> getCommentsByPost(@PathVariable Long postId) {
        logger.info("Getting comments of post {}", postId);
        MessageResponse messageResponse = commentService.getCommentsByPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> getCommentDetails(@PathVariable Long commentId) {
        logger.info("Getting comment {}", commentId);
        MessageResponse messageResponse = commentService.getCommentDetails(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

}
