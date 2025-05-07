package com.custempmanag.marketing.config;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        try {
            // Check if the default FirebaseApp instance already exists
            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing Firebase app...");
                FileInputStream serviceAccount = new FileInputStream("C:/Users/DELL/IdeaProjects/marketing/src/main/resources/labproject-ae1c5-config.json");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                return FirebaseApp.initializeApp(options);
            } else {
                // Return the existing instance if it's already initialized
                logger.info("Firebase app already initialized.");
                return FirebaseApp.getInstance();
            }
        } catch (IllegalStateException e) {
            logger.error("Error initializing FirebaseApp: {}", e.getMessage(), e);
            throw new RuntimeException("Error initializing FirebaseApp", e);
        }
    }
}