package com.custempmanag.marketing.permissionEvaluator;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.permissionEvaluator.evaluator.PermissionHandler;
import com.custempmanag.marketing.repository.OfferingRepository;
import com.custempmanag.marketing.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("RatingPermissionHandler")
@RequiredArgsConstructor
public class RatingPermissionHandler implements PermissionHandler {

//    private final RatingRepository ratingRepository;
//    private final MessageSource messageSource;

    @Override
    public boolean canAdd(Authentication authentication) {
        return true;
    }

    // wo would add a logic for anything if needed

    @Override
    public boolean canView(Authentication authentication, Long offeringId) {
        // Owner can view their rating
//        return isOwner(authentication, offeringId);
        return true;
    }

    @Override
    public boolean canEdit(Authentication authentication, Long offeringId) {
//        return isOwner(authentication, offeringId);
        return true;
    }

    @Override
    public boolean canDelete(Authentication authentication, Long offeringId) {
//        return isOwner(authentication, offeringId);
        return true;
    }
/*
    private boolean isOwner(Authentication auth, Long offeringId) {
        var userDetails = (UserPrinciple) auth.getPrincipal();

        if(userDetails.isAdmin())
            return true;

        if(!exists(offeringId)) {
            throw new
                    ResourceNotFoundException(messageSource.getMessage("offering.not.found", null, LocaleContextHolder.getLocale()));
        }

        return offeringRepository.findById(offeringId)
                .map(offering -> offering.getOwner().getId().equals(userDetails.getProfileId()))
                .orElse(false);

    }

    private boolean exists(Long offeringId) {
        return offeringRepository.existsById(offeringId);
    }

 */
}
