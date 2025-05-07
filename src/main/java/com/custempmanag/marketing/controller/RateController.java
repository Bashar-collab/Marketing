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

public class RateController {
}
