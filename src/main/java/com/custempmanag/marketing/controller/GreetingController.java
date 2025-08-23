package com.custempmanag.marketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Locale;

@RestController
public class GreetingController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/api/greeting")
    public String greeting(@RequestParam String name, Locale locale) {
        // Use {0} placeholder from messages.properties
        return messageSource.getMessage("welcome", new Object[]{name}, locale);
    }
}
