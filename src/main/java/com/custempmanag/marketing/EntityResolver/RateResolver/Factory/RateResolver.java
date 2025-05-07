package com.custempmanag.marketing.EntityResolver.RateResolver.Factory;

import com.custempmanag.marketing.model.Rating;

public interface RateResolver {
    Long getId();
    Long createRate(Rating rating);
    String getReviewType();
    Object resolve(Long rateId);
}
