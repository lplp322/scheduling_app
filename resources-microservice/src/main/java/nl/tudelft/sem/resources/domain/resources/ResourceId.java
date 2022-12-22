package nl.tudelft.sem.resources.domain.resources;

import java.io.Serializable;
import java.time.LocalDate;

public class ResourceId implements Serializable {

    private String faculty;
    private LocalDate date;

    public ResourceId(String faculty, LocalDate date) {
        this.faculty = faculty;
        this.date = date;
    }
}
