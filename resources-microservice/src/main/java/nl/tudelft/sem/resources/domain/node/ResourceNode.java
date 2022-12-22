package nl.tudelft.sem.resources.domain.node;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;

import javax.persistence.*;
import java.net.URL;
import java.time.LocalDate;

@Entity
@Table(name = "resourcenode")
@NoArgsConstructor
public class ResourceNode {

    @Column(name = "token", nullable = false)
    @Getter
    private String token;

    @Id
    @Column(name = "name", nullable = false)
    @Getter
    private String name;

    @Embedded
    @Column(name = "resources", nullable = false)
    @Getter
    private ResourcesDatabaseModel resources;

    @Column(name = "URL", nullable = false)
    @Getter
    private java.net.URL URL;

    @Column(name = "takeOfflineOn", nullable = true)
    @Setter
    @Getter
    private LocalDate takeOfflineOn;

    @Column(name = "netId", nullable = false)
    @Getter
    private String netId;

    @Column(name = "faculty", nullable = false)
    @Getter
    private String faculty;

    public ResourceNode(String token, String name, ResourcesDatabaseModel resources, URL URL, String netId, String faculty) {
        this.token = token;
        this.name = name;
        this.resources = resources;
        this.URL = URL;
        this.netId = netId;
        this.faculty = faculty;
        this.takeOfflineOn = null;
    }
}
