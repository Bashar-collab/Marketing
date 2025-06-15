package com.custempmanag.marketing.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.request.OfferingFilterRequest;
import com.custempmanag.marketing.request.OfferingRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.service.OfferingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/*
    Product Management APIs (Owner Role Required):

    Create Product:
        POST /api/products: Accepts product details (name, description, price, availability, etc.) and creates a new product associated with the logged-in owner.
    Get Product Details:
        GET /api/products/{productId}: Retrieves details of a specific product.
    Update Product:
        PUT /api/products/{productId}: Updates details of a specific product (requires owner authentication for that product).
    Delete Product:
        DELETE /api/products/{productId}: Deletes a specific product (requires owner authentication for that product).
    List Owner's Products:
        GET /api/owners/{ownerId}/products: Retrieves a list of products belonging to a specific owner.
        GET /api/users/me/products: Retrieves a list of products belonging to the currently logged-in owner (requires authentication).
 */

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER') || hasRole('CUSTOMER') or hasPermission(null, null, 'EV')")
public class OfferingController {
    private final OfferingService offeringService;

    private static final Logger logger = LoggerFactory.getLogger(OfferingController.class);

    @PostMapping(value = "/offerings", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(null, 'Offering', 'A0') or hasPermission(null, 'Offering', 'A1')")
    public ResponseEntity<MessageResponse> createOffering
            (@RequestPart("offering") @Valid OfferingRequest offeringRequest,
             @RequestPart(value = "photo", required = false) List<MultipartFile> photos,
             @AuthenticationPrincipal UserPrinciple currentUser) {

        logger.info("Creating offering for user {}", currentUser.getUsername());
        MessageResponse messageResponse = offeringService.createOffering(offeringRequest, currentUser, photos);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);

    }

    @GetMapping("/offerings")
    @PreAuthorize("hasPermission(null, 'Offering', 'S0') or hasPermission(null, 'Offering', 'S1')")
    public ResponseEntity<MessageResponse> getOfferings(@ModelAttribute OfferingFilterRequest filterRequest) {
        logger.info("Retrieving all offerings with filters");
        Pageable pageable = createPageable(filterRequest.getPage(), filterRequest.getSize(), 
            filterRequest.getSortBy(), filterRequest.getDirection());
            
        MessageResponse messageResponse = offeringService.getOfferingsWithFilters(
            filterRequest,
            pageable);
            
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/offerings/{offeringId}")
    @PreAuthorize("hasPermission(null, 'Offering', 'E0') or hasPermission(null, 'Offering', 'E1')")
    public ResponseEntity<MessageResponse> getOfferingById(@PathVariable Long offeringId) {
        logger.info("Fetching offering with id {}", offeringId);
        MessageResponse messageResponse = offeringService.getOffering(offeringId);

        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PutMapping(value ="/offerings/{offeringId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#offeringId, 'Offering', 'E0') or hasPermission(#offeringId, 'Offering', 'E1')")
    public ResponseEntity<MessageResponse> updateOffering(@PathVariable Long offeringId,
                                                          @RequestPart("offering") @Valid OfferingRequest offeringRequest,
                                                          @RequestPart(value = "photo", required = false) List<MultipartFile> photos)
    {
        logger.info("Updating offering with id {}", offeringId);
        MessageResponse messageResponse = offeringService.updateOffering(offeringId, offeringRequest, photos);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @DeleteMapping("/offerings/{offeringId}")
    @PreAuthorize("hasPermission(#offeringId, 'Offering', 'D0') or hasPermission(#offeringId, 'Offering', 'D1')")
    public ResponseEntity<MessageResponse> deleteOffering(@PathVariable Long offeringId) {
        logger.info("Deleting offering with id {}", offeringId);
        MessageResponse messageResponse = offeringService.deleteOffering(offeringId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/owners/{ownerId}/offerings")
    @PreAuthorize("hasPermission(null, 'Offering', 'S0') or hasPermission(null, 'Offering', 'S1')")
    public ResponseEntity<MessageResponse> getOfferingsByOwnerId(
            @PathVariable Long ownerId,
            @ModelAttribute OfferingFilterRequest filterRequest) {
        
        logger.info("Fetching offerings by owner id {} with filters", ownerId);
        Pageable pageable = createPageable(filterRequest.getPage(), filterRequest.getSize(), 
            filterRequest.getSortBy(), filterRequest.getDirection());
            
        MessageResponse messageResponse = offeringService.getOfferingsByOwner(
            ownerId,
            filterRequest,
            pageable);
            
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/users/offerings")
    public ResponseEntity<MessageResponse> getOfferingsByUserId(
            @AuthenticationPrincipal UserPrinciple currentUser,
            @ModelAttribute OfferingFilterRequest filterRequest) {
        
        logger.info("Fetching offerings by user {} with filters", currentUser.getId());
        Pageable pageable = createPageable(filterRequest.getPage(), filterRequest.getSize(), 
            filterRequest.getSortBy(), filterRequest.getDirection());
            
        MessageResponse messageResponse = offeringService.getOfferingsByUser(
            currentUser, 
            filterRequest,
            pageable);
            
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        // Convert 1-based page to 0-based page
        int zeroBasedPage = Math.max(0, page - 1);
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        return PageRequest.of(zeroBasedPage, size, Sort.by(sortDirection, sortBy));
    }

//    @GetMapping("/view-offering")

//    @PostMapping("/add-offering")

//    @PutMapping("/edit-offering")

//    @DeleteMapping("/delete-offering")

//    @PostMapping("/")
}
