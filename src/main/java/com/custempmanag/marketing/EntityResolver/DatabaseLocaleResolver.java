package com.custempmanag.marketing.EntityResolver;

import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolverContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.servlet.LocaleContextResolver;

import java.util.Locale;
import java.util.Optional;

public class DatabaseLocaleResolver implements LocaleContextResolver {

    private final UserRepository userRepository;
    private final Locale defaultLocale;

    public DatabaseLocaleResolver(UserRepository userRepository, String defaultLang) {
        this.userRepository = userRepository;
        this.defaultLocale = Locale.forLanguageTag(defaultLang);
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return resolveUserLocale(request);
    }

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        Locale locale = resolveUserLocale(request);
        return new SimpleLocaleContext(locale);
    }

    private Locale resolveUserLocale(HttpServletRequest request) {
//        // 1. Check for authenticated user (Spring Security)
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated()) {
//            return getFallbackLocale(request);
//        }
//
//        // 2. Fetch language from DB (cached if possible)
//        Optional<User> user = userRepository.findByUsername(auth.getName());
////                         .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//            if (user.isPresent() && user.get().getPreferredLanguage() != null) {
//                return Locale.forLanguageTag(user.get().getPreferredLanguage());
//        }

        // 3. Fallback to Accept-Language header or default
        return getFallbackLocale(request);
    }

    private Locale getFallbackLocale(HttpServletRequest request) {
        // Delegates to Accept-Language header
        return request.getLocale() != null ? request.getLocale() : defaultLocale;
    }

    @Override
    public void setLocaleContext(
            HttpServletRequest request,
            HttpServletResponse response,
            LocaleContext localeContext
    ) {
        throw new UnsupportedOperationException("Use UserService.updateLanguage() instead");
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException("Use UserService.updateLanguage() instead");
    }
}
