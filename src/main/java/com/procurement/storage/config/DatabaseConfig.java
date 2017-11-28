package com.procurement.storage.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.procurement.storage.model.entity")
@EnableJpaRepositories(basePackages = "com.procurement.storage.repository")
@EnableTransactionManagement
public class DatabaseConfig {
}
