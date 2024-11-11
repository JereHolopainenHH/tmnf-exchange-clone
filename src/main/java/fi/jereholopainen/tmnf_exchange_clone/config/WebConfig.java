package fi.jereholopainen.tmnf_exchange_clone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import fi.jereholopainen.tmnf_exchange_clone.util.TimeUtils;

@Configuration
public class WebConfig {

    @Bean
    public TimeUtils timeUtils() {
        return new TimeUtils();
    }
}