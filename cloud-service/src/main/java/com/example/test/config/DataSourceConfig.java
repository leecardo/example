//package com.example.test.config;
//
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableJpaRepositories(basePackages = "com.example.repository")
//public class DataSourceConfig {
//    @Bean
//    public DataSource dataSource() {
//        return DataSourceBuilder.create()
//                .url("jdbc:mariadb://localhost:3306/your_database")
//                .username("your_username")
//                .password("your_password")
//                .driverClassName("org.mariadb.jdbc.Driver")
//                .build();
//    }
//}
