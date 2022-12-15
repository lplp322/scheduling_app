package nl.tudelft.sem.common.models.providers;

import java.time.LocalDate;

public class TimeProvider {

    public static LocalDate now() {
        return LocalDate.now();
    }
}
