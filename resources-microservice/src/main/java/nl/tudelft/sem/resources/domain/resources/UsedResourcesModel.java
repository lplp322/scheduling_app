package nl.tudelft.sem.resources.domain.resources;

import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@IdClass(ResourceId.class)
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

    public UsedResourcesModel(String faculty, LocalDate date, ResourcesDatabaseModel resources){
        this.faculty = faculty;
        this.date = date;
        this.resources = resources;
    }

    public UsedResourcesModel(String faculty, LocalDate date, int cpu, int gpu, int ram) {
        this.faculty = faculty;
        this.date = date;
        this.resources = new ResourcesDatabaseModel(cpu, gpu, ram);
    }
}
