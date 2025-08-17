package com.custempmanag.marketing.permissionEvaluator;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.exception.DenyAccessException;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Image;
import com.custempmanag.marketing.model.Offering;
import com.custempmanag.marketing.permissionEvaluator.evaluator.PermissionHandler;
import com.custempmanag.marketing.repository.ImageRepository;
import com.custempmanag.marketing.repository.OfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component("ImagePermissionHandler")
@RequiredArgsConstructor
public class ImagePermissionHandler implements PermissionHandler {

    private final ImageRepository imageRepository;

    private final OfferingRepository offeringRepository;

    @Override
    public boolean canAdd(Authentication authentication) {
        return true;
    }

    @Override
    public boolean canView(Authentication authentication, Long targetId) {
        return true;
    }

    @Override
    public boolean canEdit(Authentication authentication, Long targetId) {
        return true;
    }

    @Override
    public boolean canDelete(Authentication authentication, Long imageId) {
        return isOwner(authentication, imageId);
    }

    private boolean isOwner(Authentication auth, Long imageId) {
        var userDetails = (UserPrinciple) auth.getPrincipal();

        if (userDetails.isAdmin())
            return true;

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        if (!"offering".equals(image.getImageableType())) {
            throw new CustomException("You can't delete this image");
        }

        Offering offering = offeringRepository.findById(image.getImageableId())
                .orElseThrow(() -> new ResourceNotFoundException("Related offering not found"));

        // Check if the current user is the owner or admin
//        return offering.getOwner().getId().equals(userDetails.getProfileId());
        return offering.getOwner().getId().equals(522);

    }
}
