package com.procurement.storage.config;

import com.procurement.storage.utils.DateUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        CassandraConfig.class,
        ServiceConfig.class,
        WebConfig.class
})
public class ApplicationConfig {

    @Bean
    public DateUtil dateUtil() {
        return new DateUtil();
    }
}
