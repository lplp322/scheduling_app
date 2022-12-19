package nl.tudelft.sem.common.models.providers;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@NoArgsConstructor
public class TimeProvider {

    public LocalDate now() {
        return LocalDate.now();
    }
}
