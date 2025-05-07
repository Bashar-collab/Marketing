package com.custempmanag.marketing.service;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Category;
import com.custempmanag.marketing.model.Offering;
import com.custempmanag.marketing.model.Owner;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.CategoryRepository;
import com.custempmanag.marketing.repository.OfferingRepository;
import com.custempmanag.marketing.request.OfferingRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.response.OfferingResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferingService {

    private final OfferingRepository offeringRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final OwnerService ownerService;

    private final CategoryRepository categoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(OfferingService.class);

    @Transactional
    public MessageResponse createOffering(OfferingRequest offeringRequest, UserPrinciple currentUser) {
        logger.info("Creating offering for id {}", currentUser.getId());
        User user = userService.validateAndGetUserById(currentUser.getId());
        logger.info("User id {}", user.getId());
        Owner owner = ownerService.getUserById(user.getProfileId());
        Offering offering = modelMapper.map(offeringRequest, Offering.class);
        offering.setOwner(owner);

        Category category = categoryRepository.findByName(offeringRequest.getCategoryName())
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));

        offering.setCategory(category);
        Offering savedOffering = offeringRepository.save(offering);
        return new MessageResponse(HttpStatus.CREATED.toString(), "Offering is created succesfully!", modelMapper.map(savedOffering, OfferingResponse.class));
//        return modelMapper.map(savedOffering, OfferingResponse.class);
    }

    public MessageResponse getOfferings()
    {
        List<Offering> offerings = offeringRepository.findAll();
        return new MessageResponse(HttpStatus.CREATED.toString(), "Offerings are retrieved successfully!", offerings.stream()
                .map(offering -> modelMapper.map(offering, OfferingResponse.class))
                .collect(Collectors.toList()));
    }

    public MessageResponse getOffering(Long offeringId) {
        logger.info("Get offering for id {}", offeringId);
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(()-> new ResourceNotFoundException("Offering is not found"));

        return new MessageResponse(HttpStatus.OK.toString(), "Offering is retrieved successfully", modelMapper.map(offering, OfferingResponse.class));

    }

    @Transactional
    public MessageResponse updateOffering(Long offeringId, OfferingRequest offeringRequest) {
        logger.info("Updating offering for id {}", offeringId);
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(()-> new ResourceNotFoundException("Offering is not found"));

        Category category = categoryRepository.findByName(offeringRequest.getCategoryName())
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));

        modelMapper.map(offeringRequest, offering);
        Offering savedOffering = offeringRepository.save(offering);
        return new MessageResponse(HttpStatus.OK.toString(), "Offering is updated successfully!", modelMapper.map(savedOffering, OfferingResponse.class));
    }

    @Transactional
    public MessageResponse deleteOffering(Long offeringId) {
        logger.info("Deleting offering for id {}", offeringId);
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(()-> new ResourceNotFoundException("Offering is not found"));

        offeringRepository.delete(offering);
        return new MessageResponse(HttpStatus.OK.toString(), "Offering is deleted successfully", null);
    }

    public MessageResponse getOfferingsByOwner(Long ownerId) {
        logger.info("Get offering by owner for id {}", ownerId);
        List<Offering> offerings = offeringRepository.findByOwnerId(ownerId);
        return new MessageResponse(HttpStatus.OK.toString(), "Offerings are retrieved successfully!", offerings.stream()
                .map(offering -> modelMapper.map(offering, OfferingResponse.class))
                .collect(Collectors.toList()));
    }

    public MessageResponse getOfferingsByUser(UserPrinciple currentUser) {
        User user = userService.validateAndGetUserById(currentUser.getId());

        Owner owner = ownerService.getUserById(user.getProfileId());

        List<Offering> offerings = offeringRepository.findByOwnerId(owner.getId());
        return new MessageResponse(HttpStatus.OK.toString(), "Offerings are retrieved successfully!", offerings.stream()
                .map(offering -> modelMapper.map(offering, OfferingResponse.class))
                .collect(Collectors.toList()));
    }
}
