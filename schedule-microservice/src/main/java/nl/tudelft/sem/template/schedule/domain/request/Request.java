package nl.tudelft.sem.template.schedule.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Basic request information.
 */
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Request {

    @Column(name = "name", nullable = false)
    @Getter
    private String name;

    @Column(name = "description", nullable = false)
    @Getter
    private String description;

    @Column(name = "faculty", nullable = false)
    @Getter
    private String faculty;

    @Column(name = "resources", nullable = false)
    @Getter
    private Resources resources;
}
