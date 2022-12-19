package nl.tudelft.sem.template.example.requests;


import javax.persistence.Entity;
import javax.persistence.Id;
import nl.tudelft.sem.common.models.RequestStatus;

@Entity
public class UserRequest {
    @Id
    private Long id;
    private String user; //netId
    private String description;
    private String faculty;
    private RequestStatus status;

    /**
     * Constructor for Request class, that is sent by  user and will be stored in nl.tudelft.sem.template.example.database.
     *
     * @param id - id of request provided from Waiting List
     * @param user        - netId of user
     * @param description - description of request
     * @param faculty - string of faculty
     * @param status - status of this request
     */
    public UserRequest(Long id, String user, String description, String faculty,
                        RequestStatus status) {
        this.id = id;
        this.user = user;
        this.description = description;
        this.faculty = faculty;
        this.status = status;
    }

    /**
     * Empty constructor for JPA repository.
     */
    public UserRequest() {
    }

    public String getUser() {
        return user;
    }

    public String getDescription() {
        return description;
    }

    public String getFaculty() {
        return faculty;
    }

    public Long getId() {
        return id;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
