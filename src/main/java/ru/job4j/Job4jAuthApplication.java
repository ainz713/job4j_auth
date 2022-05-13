package ru.job4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;



@SpringBootApplication
public class Job4jAuthApplication extends SpringBootServletInitializer {

    @Bean
    public RestTemplate getTemplate() {
        return new RestTemplate();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Job4jAuthApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Job4jAuthApplication.class, args);
    }
}
