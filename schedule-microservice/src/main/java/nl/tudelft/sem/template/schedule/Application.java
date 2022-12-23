package nl.tudelft.sem.template.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Profile;

/**
 * Example microservice application.
 */
@SpringBootApplication(scanBasePackages = {"nl.tudelft.sem.template.schedule", "nl.tudelft.sem.common"})
@EnableFeignClients(basePackages = {"nl.tudelft.sem.template.schedule.external"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
