package com.custempmanag.marketing.EntityResolver.RateResolver;

import com.custempmanag.marketing.model.Rating;

public interface RateResolver {
    Long getId();
    Long createRate(Rating rating);
    String getReviewType();
    Object resolve(Long rateId);
}
