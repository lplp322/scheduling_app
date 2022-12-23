package nl.tudelft.sem.template.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Example microservice application.
 */
@SpringBootApplication
@EntityScan
@EnableFeignClients(basePackages = {"nl.tudelft.sem.template.example.feigninterfaces"})
public class UserApp {
    public static void main(String[] args) {
        SpringApplication.run(UserApp.class, args);
    }
}
