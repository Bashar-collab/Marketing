package com.custempmanag.marketing.controller;

/*
    Rating APIs:

    Rate Product:
        POST /api/products/{productId}/ratings: Accepts a rating value and records the rating from the logged-in customer for the specified product.
    Get Average Product Rating:
        GET /api/products/{productId}/ratings/average: Retrieves the average rating for a specific product.
    Get User's Rating for Product:
        GET /api/products/{productId}/ratings/me: Retrieves the logged-in user's rating for a specific product (requires authentication).
    Rate Post:
        POST /api/posts/{postId}/ratings: Accepts a rating value and records the rating from the logged-in customer for the specified post.
    Get Average Post Rating:
        GET /api/posts/{postId}/ratings/average: Retrieves the average rating for a specific post.
    Get User's Rating for Post:
        GET /api/posts/{postId}/ratings/me: Retrieves the logged-in user's rating for a specific post (requires authentication).
    Rate Comment:
        POST /api/comments/{commentId}/ratings: Accepts a rating value and records the rating from the logged-in user for the specified comment.
    Get Average Comment Rating:
        GET /api/comments/{commentId}/ratings/average: Retrieves the average rating for a specific comment.
    Get User's Rating for Comment:
        GET /api/comments/{commentId}/ratings/me: Retrieves the logged-in user's rating for a specific comment (requires authentication).

        NEED TO DESIGN RATING FOR OWNER AND FOR USER ......
 */

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.request.RatingRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER') || hasRole('CUSTOMER')")
public class RatingController {

    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);

    private final RatingService ratingService;

    @PostMapping("/offerings/{offeringId}/ratings")
    public ResponseEntity<MessageResponse> addOfferingRating(@PathVariable Long offeringId,
                                                     @Valid @RequestBody RatingRequest ratingRequest,
                                                     @AuthenticationPrincipal UserPrinciple currentUser) {
        MessageResponse messageResponse = ratingService.addOfferingRating(offeringId, ratingRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @GetMapping("/offerings/{offeringId}/ratings")
    public ResponseEntity<MessageResponse> getOfferingRatings(@PathVariable Long offeringId){
        MessageResponse messageResponse = ratingService.getOfferingRatings(offeringId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/offerings/{offeringId}/ratings/average")
    public ResponseEntity<MessageResponse> getAverageOfferingRating(@PathVariable Long offeringId) {
        MessageResponse messageResponse = ratingService.getAverageOfferingRating(offeringId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/users/offerings/{offeringId}/ratings")
    public ResponseEntity<MessageResponse> getOfferingRatingByUser(@PathVariable Long offeringId,
                                                            @AuthenticationPrincipal UserPrinciple currentUser) {
        MessageResponse messageResponse = ratingService.getOfferingRatingByUser(offeringId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PostMapping("/posts/{postId}/ratings")
    public ResponseEntity<MessageResponse> addPostRating(@PathVariable Long postId,
                                                     @Valid @RequestBody RatingRequest ratingRequest,
                                                     @AuthenticationPrincipal UserPrinciple currentUser) {
        MessageResponse messageResponse = ratingService.addPostRating(postId, ratingRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @GetMapping("/posts/{postId}/ratings")
    public ResponseEntity<MessageResponse> getPostRatings(@PathVariable Long postId){
        MessageResponse messageResponse = ratingService.getPostRatings(postId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/posts/{postId}/ratings/average")
    public ResponseEntity<MessageResponse> getAveragePostRating(@PathVariable Long postId) {
        MessageResponse messageResponse = ratingService.getAveragePostRating(postId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/users/posts/{postId}/ratings")
    public ResponseEntity<MessageResponse> getPostRatingByUser(@PathVariable Long postId,
                                                                   @AuthenticationPrincipal UserPrinciple currentUser) {
        MessageResponse messageResponse = ratingService.getPostRatingByUser(postId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PostMapping("/comments/{commentId}/ratings")
    public ResponseEntity<MessageResponse> addCommentRating(@PathVariable Long commentId,
                                                     @Valid @RequestBody RatingRequest ratingRequest,
                                                     @AuthenticationPrincipal UserPrinciple currentUser) {
        MessageResponse messageResponse = ratingService.addCommentRating(commentId, ratingRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @GetMapping("/comments/{commentId}/ratings")
    public ResponseEntity<MessageResponse> getCommentRatings(@PathVariable Long commentId){
        MessageResponse messageResponse = ratingService.getCommentRatings(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/comments/{commentId}/ratings/average")
    public ResponseEntity<MessageResponse> getAverageCommentRating(@PathVariable Long commentId) {
        MessageResponse messageResponse = ratingService.getAverageCommentRating(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/users/comments/{commentId}/ratings")
    public ResponseEntity<MessageResponse> getCommentRatingByUser(@PathVariable Long commentId,
                                                                   @AuthenticationPrincipal UserPrinciple currentUser) {
        MessageResponse messageResponse = ratingService.getCommentRatingByUser(commentId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }
}
