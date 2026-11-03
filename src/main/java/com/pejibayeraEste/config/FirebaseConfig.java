package com.pejibayeraEste.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.storage.bucket}")
    private String bucketName;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return GoogleCredentials.getApplicationDefault();
    }

    @Bean
    public FirebaseApp firebaseApp(GoogleCredentials credentials) {

        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setStorageBucket(bucketName)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public Storage firebaseStorage(GoogleCredentials credentials) {
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }
}