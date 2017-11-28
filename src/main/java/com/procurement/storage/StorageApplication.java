package com.procurement.storage;

import com.procurement.storage.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication(scanBasePackageClasses = {ApplicationConfig.class},
    exclude = {LiquibaseAutoConfiguration.class})
public class StorageApplication {
    public static void main(final String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }
}
