package nl.tudelft.sem.waitinglist.domain;

import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RequestValidator {

    /**
     * Validates the name of a request.
     *
     * @param name request name
     * @return validated name
     *
     * @throws IllegalArgumentException if name is not valid
     */
    public static String validateName(@NonNull String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Request name cannot be blank");
        }

        return name;
    }

    /**
     * Validates the description of a request.
     *
     * @param description request description
     * @return validated description
     *
     * @throws IllegalArgumentException if description is not valid
     */
    public static String validateDescription(@NonNull String description) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("Request description cannot be blank");
        }

        return description;
    }

    /**
     * Validates the faculty of a request.
     *
     * @param faculty request faculty
     * @return validated faculty
     *
     * @throws IllegalArgumentException if faculty is not valid
     */
    public static String validateFaculty(@NonNull String faculty) {
        if (faculty.isBlank()) {
            throw new IllegalArgumentException("Request faculty cannot be blank");
        }

        return faculty;
    }

    /**
     * Validates the deadline of a request.
     *
     * @param deadline request deadline
     * @param currentDateTime current date/time
     * @return validated deadline
     *
     * @throws IllegalArgumentException if deadline is not valid
     */
    public static LocalDate validateDeadline(LocalDate deadline, LocalDateTime currentDateTime) {
        if (deadline == null) {
            return null;
        }

        LocalDate currentDate = currentDateTime.toLocalDate();
        if (!deadline.isAfter(currentDate)) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }
        if (deadline.isEqual(currentDate.plusDays(1))
                && !currentDateTime.isBefore(currentDate.atTime(23, 55))) {
            throw new IllegalArgumentException("Deadline cannot be set to next day "
                    + "less than 5 minutes before start of day");
        }

        return deadline;
    }
}
