package com.custempmanag.marketing.controller;


/*
    Post Management APIs (Owner Role Required):

    Create Post:
        POST /api/posts: Accepts post details (owner ID, product ID, title, content) and creates a new post.
    Get Post Details:
        GET /api/posts/{postId}: Retrieves details of a specific post, including associated product information and comments.
    Update Post:
        PUT /api/posts/{postId}: Updates details of a specific post (requires owner authentication for that post).
    Delete Post:
        DELETE /api/posts/{postId}: Deletes a specific post (requires owner authentication for that post).
    List Owner's Posts:
        GET /api/owners/{ownerId}/posts: Retrieves a list of posts created by a specific owner.
        GET /api/users/me/posts: Retrieves a list of posts created by the currently logged-in owner (requires authentication).
    List Product's Posts:
        GET /api/products/{productId}/posts: Retrieves a list of posts related to a specific product.
 */

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.request.CreatePostRequest;
import com.custempmanag.marketing.request.UpdatePostRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.response.PostResponse;
import com.custempmanag.marketing.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@PreAuthorize("hasRole('OWNER')")
public class PostController {

    private final PostService postService;

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @PostMapping("/posts")
    public ResponseEntity<MessageResponse> createPost(@Valid @RequestBody CreatePostRequest createPostRequest,
                                        @AuthenticationPrincipal UserPrinciple currentUser) {
        logger.info("Create post for user: {}", currentUser.getUsername());
        MessageResponse messageResponse = postService.createPost(createPostRequest, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);

    }

    @GetMapping("/posts")
    public ResponseEntity<MessageResponse> getPosts(){
        logger.info("Get all posts");
        MessageResponse messageResponse = postService.getAllPosts();
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<MessageResponse> getPostById(@PathVariable Long id) {
        logger.info("Fetching post with id {}", id);
        MessageResponse messageResponse = postService.getPost(id);

        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    // DON'T FORGET THESE TMW
    @PutMapping("/posts/{postId}")
    public ResponseEntity<MessageResponse> updatePost(@PathVariable Long postId, @Valid @RequestBody UpdatePostRequest updatePostRequest)
    {
        logger.info("Updating post with id {}", postId);
        MessageResponse messageResponse = postService.updatePost(postId, updatePostRequest);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable Long postId) {
        logger.info("Deleting post with id {}", postId);
        MessageResponse messageResponse = postService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    // MUST CHECK THIS ISSUE LATER
    //              /owners/2/posts
    @GetMapping("/owners/{ownerId}/posts")
    public ResponseEntity<MessageResponse> getPostsByOwnerId(@PathVariable Long ownerId) {
        logger.info("Fetching posts by owner id {}", ownerId);
        MessageResponse messageResponse = postService.getPostsByOwner(ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/users/posts")
    public ResponseEntity<MessageResponse> getPostsByUserId(@AuthenticationPrincipal UserPrinciple currentUser) {
        logger.info("Fetching posts by user {}", currentUser.getId());
        MessageResponse messageResponse = postService.getPostsByUser(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    // MUST CHECK THIS ISSUE LATER
    @GetMapping("/api/offerings/{offeringId}/posts")
    public ResponseEntity<MessageResponse> getPostsByOfferingId(@PathVariable Long offeringId) {
        logger.info("Fetching posts by offering id {}", offeringId);
        MessageResponse messageResponse = postService.getPostsByOfferingId(offeringId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }
    /*

     */
}
