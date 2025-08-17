package com.custempmanag.marketing.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.custempmanag.marketing.request.OfferingFilterRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Category;
import com.custempmanag.marketing.model.CurrencyTypes;
import com.custempmanag.marketing.model.Image;
import com.custempmanag.marketing.model.Offering;
import com.custempmanag.marketing.model.Owner;
import com.custempmanag.marketing.query.OfferingSpecification;
import com.custempmanag.marketing.repository.CategoryRepository;
import com.custempmanag.marketing.repository.ImageRepository;
import com.custempmanag.marketing.repository.OfferingRepository;
import com.custempmanag.marketing.repository.RatingRepository;
import com.custempmanag.marketing.request.OfferingRequest;
import com.custempmanag.marketing.response.ImageResponse;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.response.OfferingResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class OfferingService {

    private final OfferingRepository offeringRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final OwnerService ownerService;

    private final CategoryRepository categoryRepository;

    private final MessageSource messageSource;

    private final ImageService imageService;

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(OfferingService.class);
    private final ImageRepository imageRepository;
    private final RatingRepository ratingRepository;


    @CacheEvict(value = {"offerings", "ownerOfferings", "usersOfferings"}, allEntries = true)
    @Transactional
    public MessageResponse createOffering(OfferingRequest offeringRequest,
                                          UserPrinciple currentUser,
                                          List<MultipartFile> photos) {
        logger.info("Creating offering for id {}", currentUser.getId());

        Owner owner = ownerService.getUserById(currentUser.getId());

        Category category = categoryRepository.findByName(offeringRequest.getCategoryName())
                .orElseThrow(() -> new
                        ResourceNotFoundException(getMessage("category.not.found")));


        Offering offering = modelMapper.map(offeringRequest, Offering.class);
        offering.setOwner(owner);

        offering.setCategory(category);
        Offering savedOffering = offeringRepository.save(offering);

        if (photos != null && !photos.isEmpty())
        {
            for (MultipartFile photo : photos)
                imageService.savePhoto(savedOffering, photo, savedOffering.getClass().getSimpleName().toLowerCase());
        }

        return buildSuccessResponse("offering.create.success",
                modelMapper.map(savedOffering, OfferingResponse.class));
    }

    @Cacheable(value = "offerings")
    public MessageResponse getOfferings(Pageable pageable) {
        Page<OfferingResponse> offerings = offeringRepository.getAllOfferings(pageable);

        if (offerings.isEmpty())
            return buildSuccessResponse("offering.getAll.empty", Collections.emptyList());

        List<OfferingResponse> offeringResponses = getImages(offerings.getContent());

        Map<String, Object> response = Map.of(
            "content", offeringResponses,
            "totalElements", offerings.getTotalElements(),
            "totalPages", offerings.getTotalPages(),
            "currentPage", offerings.getNumber() + 1,
            "pageSize", offerings.getSize(),
            "isFirst", offerings.isFirst(),
            "isLast", offerings.isLast()
        );

        return buildSuccessResponse("offering.getAll.success", response);
    }

    @Cacheable(
            value = "offerings",
            key ="'offerings::' + T(java.util.Objects).hash(#offeringFilterRequest)"
    )
    public MessageResponse getOfferingsWithFilters(OfferingFilterRequest offeringFilterRequest,
                                                   Pageable pageable) {
        return getFilteredOfferings(null, offeringFilterRequest, pageable);
    }

    @Cacheable(value = "offering", key = "#offeringId")
    public MessageResponse getOffering(Long offeringId) {
        logger.info("Get offering for id {}", offeringId);
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new
                        ResourceNotFoundException(getMessage("offering.not.found")));

        OfferingResponse response = modelMapper.map(offering, OfferingResponse.class);

        Double avgRating = ratingRepository.findAverageByRateableIdAndType(offeringId, "Offering");
        response.setRatingValue(avgRating);

        List<Image> images = imageRepository.findPathByImageableIdAndImageableType(offeringId, "offering");
        List<ImageResponse> imageResponses = images.stream()
                .map(img -> new ImageResponse(
                        img.getId(),
                        imageService.getImagePath(img)
                ))
                .collect(Collectors.toList());

        response.setImage(imageResponses);

        return buildSuccessResponse("offering.get.success", response);
    }

    @CacheEvict(value = {"offerings", "offering", "ownerOfferings", "userOfferings"}, allEntries = true)
    @Transactional
    public MessageResponse updateOffering(Long offeringId, OfferingRequest offeringRequest, List<MultipartFile> photos) {
        logger.info("Updating offering for id {}", offeringId);

        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new
                        ResourceNotFoundException(getMessage("offering.not.found")));


        Category category = categoryRepository.findByName(offeringRequest.getCategoryName())
                .orElseThrow(() -> new
                        ResourceNotFoundException(getMessage("category.not.found")));

        modelMapper.map(offeringRequest, offering);
        Offering savedOffering = offeringRepository.save(offering);

        if (photos != null && !photos.isEmpty())
        {
            for (MultipartFile photo : photos)
                imageService.savePhoto(savedOffering, photo, savedOffering.getClass().getSimpleName().toLowerCase());
        }

        return buildSuccessResponse("offering.update.success",
                modelMapper.map(savedOffering, OfferingResponse.class));
    }

    @Caching(evict = {
            @CacheEvict(value = "offering", key = "#offeringId"),
            @CacheEvict(value = "offerings", allEntries = true),
            @CacheEvict(value = "ownerOfferings", allEntries = true),
            @CacheEvict(value = "usersOfferings", allEntries = true)
    })
    @Transactional
    public MessageResponse deleteOffering(Long offeringId) {
        logger.info("Deleting offering for id {}", offeringId);

        imageRepository.deleteByImageableIdAndImageableType(offeringId, "offering");
        ratingRepository.deleteByRateableIdAndRateableType(offeringId, "Offering");

        offeringRepository.deleteById(offeringId);
        return buildSuccessResponse("offering.delete.success", null);
    }

    @Cacheable(value = "ownerOfferings", key = "#ownerId + '-' + T(java.util.Objects).hash(#offeringFilterRequest)")
    public MessageResponse getOfferingsByOwner(Long ownerId,
                                               OfferingFilterRequest offeringFilterRequest,
                                               Pageable pageable) {
        return getFilteredOfferings(ownerId, offeringFilterRequest, pageable);
    }

    @Cacheable(value = "userOfferings", key = "#currentUser.id + '-' + T(java.util.Objects).hash(#offeringFilterRequest)")
    public MessageResponse getOfferingsByUser(UserPrinciple currentUser,
                                              OfferingFilterRequest offeringFilterRequest,
                                              Pageable pageable) {
        Owner owner = ownerService.getUserById(currentUser.getId());
        return getFilteredOfferings(owner.getId(),offeringFilterRequest, pageable);
    }

    private MessageResponse getFilteredOfferings(Long ownerId,
                                                 OfferingFilterRequest offeringFilterRequest,
                                                 Pageable pageable) {
        String sortBy = pageable.getSort().stream()
                .findFirst()
                .map(order -> order.getProperty())
                .orElse("id");
                
        String direction = pageable.getSort().stream()
                .findFirst()
                .map(order -> order.getDirection().toString().toLowerCase())
                .orElse("asc");
                
        // Create a new Pageable with the correct sort direction
        Pageable modifiedPageable = pageable;
        if ("ratingValue".equals(sortBy)) {
            Sort.Direction sortDirection = "desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            modifiedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, "id"));
        }

        Page<OfferingResponse> offerings = OfferingSpecification.findByOfferingsWithFilters(
                ownerId,
                offeringFilterRequest.getName(),
                offeringFilterRequest.getMinPrice(),
                offeringFilterRequest.getMaxPrice(),
                offeringFilterRequest.getCategoryName(),
                offeringFilterRequest.getCurrency(),
                offeringFilterRequest.getMinRating(),
                sortBy,
                direction,
                modifiedPageable,
                entityManager);

        if (offerings.isEmpty())
            return buildSuccessResponse("offering.getAll.empty", Collections.emptyList());

        List<OfferingResponse> offeringResponses = getImages(offerings.getContent());

        Map<String, Object> response = Map.of(
            "content", offeringResponses,
            "totalElements", offerings.getTotalElements(),
            "totalPages", offerings.getTotalPages(),
            "currentPage", offerings.getNumber() + 1,
            "pageSize", offerings.getSize(),
            "isFirst", offerings.isFirst(),
            "isLast", offerings.isLast()
        );

        return buildSuccessResponse("offering.getAll.success", response);
    }

    private List<OfferingResponse> getImages(List<OfferingResponse> offerings) {
        List<Long> offeringIds = offerings.stream()
                .map(OfferingResponse::getId)
                .toList();

        List<Image> allImages = imageRepository.findByImageableIdInAndImageableType(offeringIds, "offering");

        Map<Long, List<ImageResponse>> imagesGroupedByOfferingId = allImages.stream()
                .collect(Collectors.groupingBy(
                        Image::getImageableId,
                        Collectors.mapping(
                                img -> new ImageResponse(img.getId(), imageService.getImagePath(img)),
                                Collectors.toList()
                        )
                ));

        return offerings.stream()
                .map(offering -> {
                    OfferingResponse response = modelMapper.map(offering, OfferingResponse.class);
                    List<ImageResponse> imageResponses = imagesGroupedByOfferingId.getOrDefault(offering.getId(), Collections.emptyList());
                    response.setImage(imageResponses);
                    return response;
                })
                .toList();
    }

    private MessageResponse buildSuccessResponse(String messageCode, Object data) {
        return new MessageResponse(HttpStatus.OK.toString(), getMessage(messageCode), data);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
