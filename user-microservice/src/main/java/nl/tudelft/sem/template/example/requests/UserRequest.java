package nl.tudelft.sem.template.example.requests;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class UserRequest {
    @Id
    @SequenceGenerator(
        name = "request_sequence",
        sequenceName = "request_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "request_sequence"
    )
    private Long id;
    private String user; //netId
    private String description;
    private double cpu;
    private double gpu;
    private double memory;
    private Date date;
    private String condition;

    /**
     * Constructor for Request class, that is sent by  user and will be stored in database.
     *
     * @param user        - netId of user
     * @param description - description of request
     * @param cpu         - amount of cpu power requested
     * @param gpu         - amount of gpu power requested
     * @param mem         - amount of memory requested
     * @param date        - due date/deadline
     */
    public UserRequest(String user, String description, double cpu, double gpu, double mem, Date date, String condition) {
        this.user = user;
        this.description = description;
        this.cpu = cpu;
        this.gpu = gpu;
        this.memory = mem;
        this.date = date;
        this.condition = condition;
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

    public Date getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public String getCondition() {
        return condition;
    }

    public double getCpu() {
        return cpu;
    }

    public double getGpu() {
        return gpu;
    }

    public double getMemory() {
        return memory;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGpu(double gpu) {
        this.gpu = gpu;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
