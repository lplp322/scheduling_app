package nl.tudelft.sem.template.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class GetDate {

    public LocalDate now() {
        return LocalDate.now();
    }
}