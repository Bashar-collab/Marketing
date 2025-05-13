package com.custempmanag.marketing.service;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.controller.CommentController;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.*;
import com.custempmanag.marketing.repository.CommentRepository;
import com.custempmanag.marketing.repository.OfferingRepository;
import com.custempmanag.marketing.repository.PostRepository;
import com.custempmanag.marketing.repository.RatingRepository;
import com.custempmanag.marketing.request.RatingRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.response.RatingResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final RatingRepository ratingRepository;
    private final UserService userService;
    private final OfferingRepository offeringRepository;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MessageSource messageSource;

    @Transactional
    public MessageResponse addOfferingRating(Long offeringId, RatingRequest ratingRequest, UserPrinciple currentUser) {

        logger.info("Adding rating for offering {}", offeringId);

        User user = userService.validateAndGetUserById(currentUser.getId());
        Offering offering = offeringRepository.findById(offeringId).
                orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("offering.not.found", null, LocaleContextHolder.getLocale())));

        saveRate(user, offering.getId(), offering.getClass().getSimpleName(), ratingRequest);
//        Rating rating = modelMapper.map(ratingRequest, Rating.class);
//        rating.setUser(user);
//        rating.setRateableId(offering.getId());
//        rating.setRateableType("offering");
//        ratingRepository.save(rating);
        logger.info("Rating added for offering {}", offeringId);
        return new MessageResponse(HttpStatus.CREATED.toString(),
                messageSource.getMessage("rating.add.success", null, LocaleContextHolder.getLocale()),
                null);
    }

    public MessageResponse getOfferingRatings(Long offeringId) {
        logger.info("Get rating for entity {}", offeringId);
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("offering.not.found", null, LocaleContextHolder.getLocale())));

        List<Rating> ratingList = ratingRepository.findRatingByRateableIdAndRateableType(offering.getId(), "Offering");

        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("rating.getAll.success", null, LocaleContextHolder.getLocale()),
                ratingList.stream()
                .map(rating -> modelMapper.map(rating, RatingResponse.class))
                .collect(Collectors.toList()));
    }

    public MessageResponse getAverageOfferingRating(Long offeringId) {
        logger.info("Getting average rating for offering {}", offeringId);
        Offering offering = offeringRepository.findById(offeringId).
                orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("offering.not.found", null, LocaleContextHolder.getLocale())));

        double Rating = ratingRepository.findAverageRatingById(offering.getId());
        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("rating.get.success", null, LocaleContextHolder.getLocale()),
                Rating);
    }

    public MessageResponse getOfferingRatingByUser(Long offeringId, UserPrinciple currentUser) {
        logger.info("Getting rating for user {}", currentUser.getId());
        User user = userService.validateAndGetUserById(currentUser.getId());
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("offering.not.found", null, LocaleContextHolder.getLocale())));

        Double rate = ratingRepository.findRatingByIdAndRateableId(user.getId(), offering.getId());
        return new
                MessageResponse(HttpStatus.OK.toString(),
                                messageSource.getMessage("rating.get.success", null, LocaleContextHolder.getLocale()),
                                rate);
    }

    @Transactional
    public MessageResponse addPostRating(Long postId, RatingRequest ratingRequest, UserPrinciple currentUser) {

        logger.info("Adding rating for post {}", postId);

        User user = userService.validateAndGetUserById(currentUser.getId());
        Post post = postRepository.findById(postId).
                orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("post.not.found", null, LocaleContextHolder.getLocale())));

        saveRate(user, post.getId(), post.getClass().getSimpleName(), ratingRequest);
//        Rating rating = modelMapper.map(ratingRequest, Rating.class);
//        rating.setUser(user);
//        rating.setRateableId(post.getId());
//        rating.setRateableType("post");
//        ratingRepository.save(rating);
        logger.info("Rating added for post {}", postId);
        return new MessageResponse(HttpStatus.CREATED.toString(),
                messageSource.getMessage("rating.add.success", null, LocaleContextHolder.getLocale()),
                null);
    }

    public MessageResponse getPostRatings(Long postId) {
        logger.info("Get rating for entity {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("post.not.found", null, LocaleContextHolder.getLocale())));

        List<Rating> ratingList = ratingRepository.findRatingByRateableIdAndRateableType(post.getId(), "Post");

        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("rating.getAll.success", null, LocaleContextHolder.getLocale()),
                ratingList.stream()
                        .map(rating -> modelMapper.map(rating, RatingResponse.class))
                        .collect(Collectors.toList()));
    }

    public MessageResponse getAveragePostRating(Long postId) {
        logger.info("Getting average rating for post {}", postId);
        Post post = postRepository.findById(postId).
                orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("post.not.found", null, LocaleContextHolder.getLocale())));

        double Rating = ratingRepository.findAverageRatingById(post.getId());
        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("rating.get.success", null, LocaleContextHolder.getLocale()),
                Rating);
    }

    public MessageResponse getPostRatingByUser(Long postId, UserPrinciple currentUser) {
        logger.info("Getting rating for user {}", currentUser.getId());
        User user = userService.validateAndGetUserById(currentUser.getId());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("post.not.found", null, LocaleContextHolder.getLocale())));

        Double rate = ratingRepository.findRatingByIdAndRateableId(user.getId(), post.getId());
        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("rating.get.success", null, LocaleContextHolder.getLocale()),
                rate);
    }

    @Transactional
    public MessageResponse addCommentRating(Long commentId, RatingRequest ratingRequest, UserPrinciple currentUser) {

        logger.info("Adding rating for comment {}", commentId);

        User user = userService.validateAndGetUserById(currentUser.getId());
        Comment comment = commentRepository.findById(commentId).
                orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("comment.not.found", null, LocaleContextHolder.getLocale())));

        saveRate(user, comment.getId(), comment.getClass().getSimpleName(), ratingRequest);

        logger.info("Rating added for comment {}", commentId);
        return new MessageResponse(HttpStatus.CREATED.toString(),
                messageSource.getMessage("rating.add.success", null, LocaleContextHolder.getLocale()),
                null);
    }

    public MessageResponse getCommentRatings(Long commentId) {
        logger.info("Get rating for entity {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("comment.not.found", null, LocaleContextHolder.getLocale())));

        List<Rating> ratingList = ratingRepository.findRatingByRateableIdAndRateableType(comment.getId(), "Comment");

        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("rating.getAll.success", null, LocaleContextHolder.getLocale()),
                ratingList.stream()
                        .map(rating -> modelMapper.map(rating, RatingResponse.class))
                        .collect(Collectors.toList()));
    }

    public MessageResponse getAverageCommentRating(Long commentId) {
        logger.info("Getting average rating for comment {}", commentId);
        Comment comment = commentRepository.findById(commentId).
                orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("comment.not.found", null, LocaleContextHolder.getLocale())));

        Double Rating = ratingRepository.findAverageRatingById(comment.getId());
        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("rating.add.success", null, LocaleContextHolder.getLocale()),
                Rating);
    }

    public MessageResponse getCommentRatingByUser(Long commentId, UserPrinciple currentUser) {
        logger.info("Getting rating for user {}", currentUser.getId());
        User user = userService.validateAndGetUserById(currentUser.getId());
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("offering.not.found", null, LocaleContextHolder.getLocale())));

        Double rate = ratingRepository.findRatingByIdAndRateableId(user.getId(), comment.getId());
        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("rating.get.success", null, LocaleContextHolder.getLocale()),
                rate);
    }

    private void saveRate(User user, Long Id, String entityName, RatingRequest ratingRequest){
        Optional<Rating> existingRating = ratingRepository.findByUserAndRateableId(user, Id);
        if(existingRating.isPresent()){
            Rating rating = existingRating.get();
            modelMapper.map(ratingRequest, rating);
            ratingRepository.save(rating);
            return;
        }
        Rating rating = modelMapper.map(ratingRequest, Rating.class);
        rating.setUser(user);
        rating.setRateableId(Id);
        rating.setRateableType(entityName);
        ratingRepository.save(rating);
    }
}
