package com.custempmanag.marketing.service;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.exception.DenyAccessException;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.*;
import com.custempmanag.marketing.repository.OfferingRepository;
import com.custempmanag.marketing.repository.PostRepository;
import com.custempmanag.marketing.request.PostRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.response.PostResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final OwnerService ownerService;

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    private final OfferingRepository offeringRepository;

    private final MessageSource messageSource;

//    @RateLimited(10) NEED TO ADD THIS IN THE FUTURE
    @Transactional
    public MessageResponse createPost(PostRequest createPostRequest, UserPrinciple currentUser) {
        logger.info("Creating offering for id {}", currentUser.getId());
        User user = userService.validateAndGetUserById(currentUser.getId());
        logger.info("User id {}", user.getId());
        Owner owner = ownerService.getUserById(user.getId());
        Post post = modelMapper.map(createPostRequest, Post.class);
        post.setOwner(owner);

        if(createPostRequest.getOfferingId() != null) {
            Offering offering = offeringRepository.findById(createPostRequest.getOfferingId())
                    .orElseThrow(() -> new
                            ResourceNotFoundException(messageSource.getMessage("offering.not.found", null, LocaleContextHolder.getLocale())));
            post.setOffering(offering);
        }

        Post savedPost = postRepository.save(post);
        return new MessageResponse(
                HttpStatus.CREATED.toString(),
                messageSource.getMessage("post.create.success", null, LocaleContextHolder.getLocale()),
                savedPost.getId());
    }

    public MessageResponse getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return new MessageResponse(
                HttpStatus.OK.toString(),
                messageSource.getMessage("post.getAll.success", null, LocaleContextHolder.getLocale()),
                posts.stream()
                .map(post -> modelMapper.map(post, PostResponse.class))
                .collect(Collectors.toList()));
    }

    public MessageResponse getPost(Long postId) {
        logger.info("Get post for id {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new
                        ResourceNotFoundException(messageSource.getMessage("post.not.found", null, LocaleContextHolder.getLocale())));

        return new MessageResponse(
                HttpStatus.OK.toString(),
                messageSource.getMessage("post.get.success", null, LocaleContextHolder.getLocale()),
                modelMapper.map(post, PostResponse.class));

    }

    @Transactional
    public MessageResponse updatePost(Long postId, PostRequest updatePostRequest, UserPrinciple currentUser) {
        logger.info("Updating post for id {}", postId);

        User user = userService.validateAndGetUserById(currentUser.getId());

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new
                        ResourceNotFoundException(messageSource.getMessage("post.not.found", null, LocaleContextHolder.getLocale())));

        if(checkOwnership(user.getId(), post.getOwner().getId()))
            throw new
                    DenyAccessException(messageSource.getMessage("forbidden", null, LocaleContextHolder.getLocale()));

//        Category category = categoryRepository.findByName(offeringRequest.getCategoryName())
//                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));

//        offering.setCategory(category);
//        modelMapper.map(updatePostRequest, post);
        post.setTitle(updatePostRequest.getTitle());
        post.setDescription(updatePostRequest.getDescription());
        post.setContent(updatePostRequest.getContent());
        Post savedPost = postRepository.save(post);
        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("post.update.success", null, LocaleContextHolder.getLocale()),
                modelMapper.map(savedPost, PostResponse.class));
    }

    @Transactional
    public MessageResponse deletePost(Long postId, UserPrinciple currentUser) {
        logger.info("Deleting post for id {}", postId);
        User user = userService.validateAndGetUserById(currentUser.getId());

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new
                        ResourceNotFoundException(messageSource.getMessage("post.not.found", null, LocaleContextHolder.getLocale())));

        if(checkOwnership(user.getId(), post.getOwner().getId()))
            throw new
                    DenyAccessException(messageSource.getMessage("forbidden", null, LocaleContextHolder.getLocale()));

        postRepository.delete(post);
        return new MessageResponse(
                HttpStatus.OK.toString(),
                messageSource.getMessage("post.delete.success", null, LocaleContextHolder.getLocale()),
                null);
    }

    public MessageResponse getPostsByOwner(Long ownerId) {
        logger.info("Get posts by owner for id {}", ownerId);
        List<Post> posts = postRepository.findByOwnerId(ownerId);
        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("post.getAll.success", null, LocaleContextHolder.getLocale()),
                posts.stream()
                .map(post -> modelMapper.map(post, PostResponse.class))
                .collect(Collectors.toList()));
    }

    public MessageResponse getPostsByUser(UserPrinciple currentUser) {
        User user = userService.validateAndGetUserById(currentUser.getId());

        Owner owner = ownerService.getUserById(user.getId());

        List<Post> posts = postRepository.findByOwnerId(owner.getId());
        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("post.getAll.success", null, LocaleContextHolder.getLocale()),
                posts.stream()
                .map(post -> modelMapper.map(post, PostResponse.class))
                .collect(Collectors.toList()));
    }

    public MessageResponse getPostsByOfferingId(Long offeringId) {
        logger.info("Get posts by product id {}", offeringId);
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(()-> new
                        ResourceNotFoundException(messageSource.getMessage("offering.not.found", null, LocaleContextHolder.getLocale())));

        List<Post> posts = postRepository.findByOfferingId(offering.getId());
        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("post.getAll.success", null, LocaleContextHolder.getLocale()),
                posts.stream()
                .map(post -> modelMapper.map(post, PostResponse.class))
                .collect(Collectors.toList()));
    }
    /*

     */

    private boolean checkOwnership(Long userId, Long ownerId) {
        return !userId.equals(ownerId);
    }
}
