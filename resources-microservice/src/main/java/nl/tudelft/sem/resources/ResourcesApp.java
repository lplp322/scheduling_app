package nl.tudelft.sem.resources;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Example microservice application.
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"nl.tudelft.sem.resources.feigninterfaces"})
public class ResourcesApp {
    public static void main(String[] args) {
        SpringApplication.run(ResourcesApp.class, args);
    }
}
