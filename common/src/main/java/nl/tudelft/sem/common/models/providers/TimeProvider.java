package nl.tudelft.sem.common.models.providers;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@NoArgsConstructor
public class TimeProvider {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
