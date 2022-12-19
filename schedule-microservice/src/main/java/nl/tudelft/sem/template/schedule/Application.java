package nl.tudelft.sem.template.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Example microservice application.
 */
@SpringBootApplication(scanBasePackages = {"nl.tudelft.sem.template.schedule", "nl.tudelft.sem.common"})
@EnableFeignClients(basePackages = {"nl.tudelft.sem.template.example.feigninterfaces"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
