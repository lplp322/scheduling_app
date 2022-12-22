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
    private java.net.URL url;

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

    /** Constructor.
     *
     * @param token access token
     * @param name node name
     * @param resources node resources
     * @param url node url
     * @param netId owner netId
     * @param faculty node faculty
     */
    public ResourceNode(String token, String name, ResourcesDatabaseModel resources, URL url, String netId, String faculty) {
        this.token = token;
        this.name = name;
        this.resources = resources;
        this.url = url;
        this.netId = netId;
        this.faculty = faculty;
    }
}
