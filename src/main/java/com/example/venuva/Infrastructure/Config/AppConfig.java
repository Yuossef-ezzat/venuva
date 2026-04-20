package com.example.venuva.Infrastructure.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * RestTemplate bean — equivalent to IHttpClientFactory in .NET.
     * Inject this into PayMobService via constructor injection.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
