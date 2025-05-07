package com.custempmanag.marketing.EntityResolver.RateResolver.Factory;

import com.custempmanag.marketing.EntityResolver.ProfileResolver.ProfileResolver;
import com.custempmanag.marketing.model.Rating;
import com.custempmanag.marketing.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RateResolverFactory {
    private final Map<String, RateResolver> resolvers = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(RateResolverFactory.class);

    @Autowired
    public RateResolverFactory(List<RateResolver> resolverList) {
        for (RateResolver resolver : resolverList) {
            resolvers.put(resolver.getReviewType(), resolver);
        }
    }

    public Long createRate(Rating rating) {
        RateResolver resolver = resolvers.get(rating.getRateableType());
        if (resolver == null) {
            throw new IllegalArgumentException("Invalid rate type: " + rating.getRateableType());
        }
        return resolver.createRate(rating);
    }
    public RateResolver getResolver(String rateableType) {
        return resolvers.get(rateableType);
    }
}
