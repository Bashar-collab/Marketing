package com.custempmanag.marketing.EntityResolver.ProfileResolver.Factory;

import com.custempmanag.marketing.EntityResolver.ProfileResolver.ProfileResolver;
import com.custempmanag.marketing.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProfileResolverFactory {
    private final Map<String, ProfileResolver> resolvers = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(ProfileResolverFactory.class);

    @Autowired
    public ProfileResolverFactory(List<ProfileResolver> resolverList) {
        for (ProfileResolver resolver : resolverList) {
            resolvers.put(resolver.getProfileType(), resolver);
        }
    }
//
//    public Long createProfile(User user) {
//        ProfileResolver resolver = resolvers.get(user.getProfileType());
//        if (resolver == null) {
//            throw new IllegalArgumentException("Invalid profile type: " + user.getProfileType());
//        }
//        return resolver.createProfile(user);
//    }
    public ProfileResolver getResolver(String profileType) {
        return resolvers.get(profileType);
    }
}
