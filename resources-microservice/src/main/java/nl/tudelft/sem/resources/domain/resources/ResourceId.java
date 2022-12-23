package nl.tudelft.sem.resources.domain.resources;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
public class ResourceId implements Serializable {

    public static final long serialVersionUID = 5678435;

    private String faculty;
    private LocalDate date;

    public ResourceId(String faculty, LocalDate date) {
        this.faculty = faculty;
        this.date = date;
    }
}
