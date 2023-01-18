package nl.tudelft.sem.resources.domain.resources;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class ResourceAllocationModel {

    @Column(name = "faculty", nullable = false)
    @Id
    private String faculty;

    @Setter
    @Embedded
    @Column(name = "resources", nullable = false)
    private ResourcesDatabaseModel resources;

    public ResourceAllocationModel(String faculty, int cpu, int gpu, int ram) {
        this.faculty = faculty;
        this.resources = new ResourcesDatabaseModel(cpu, gpu, ram);
    }
}
