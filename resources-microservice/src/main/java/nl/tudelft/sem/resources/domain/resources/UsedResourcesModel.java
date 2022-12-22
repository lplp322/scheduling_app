package nl.tudelft.sem.resources.domain.resources;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@IdClass(ResourceId.class)
@NoArgsConstructor
public class UsedResourcesModel {

    @Column(name = "faculty", nullable = false)
    @Id
    private String faculty;

    @Column(name = "date", nullable = false)
    @Id
    private LocalDate date;

    @Embedded
    @Column(name = "resources", nullable = false)
    private ResourcesDatabaseModel resources;

    /** Constructor.
     *
     * @param faculty faculty
     * @param date date
     * @param resources resources
     */
    public UsedResourcesModel(String faculty, LocalDate date, ResourcesDatabaseModel resources) {
        this.faculty = faculty;
        this.date = date;
        this.resources = resources;
    }

    /** Constructor.
     *
     * @param faculty faculty
     * @param date date
     * @param cpu cpu
     * @param gpu gpu
     * @param ram ram
     */
    public UsedResourcesModel(String faculty, LocalDate date, int cpu, int gpu, int ram) {
        this.faculty = faculty;
        this.date = date;
        this.resources = new ResourcesDatabaseModel(cpu, gpu, ram);
    }
}
