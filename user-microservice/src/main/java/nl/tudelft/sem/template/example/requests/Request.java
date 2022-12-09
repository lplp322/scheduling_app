package nl.tudelft.sem.template.example.requests;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Request {
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
    private transient String user; //netId
    private transient String description;
    private transient double cpu;
    private transient double gpu;
    private transient double mem;
    private transient Date date;
    private transient String condition;

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
    public Request(String user, String description, double cpu, double gpu, double mem, Date date, String condition) {
        this.user = user;
        this.description = description;
        this.cpu = cpu;
        this.gpu = gpu;
        this.mem = mem;
        this.date = date;
        this.condition = condition;
    }

    /**
     * Empty constructor for JPA repository.
     */
    public Request() {
    }

    /**
     * Getter.
     *
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * Getter.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter.
     *
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Getter.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter.
     *
     * @return user
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Getter.
     *
     * @return CPU
     */
    public double getCpu() {
        return cpu;
    }

    /**
     * Getter GPU.
     *
     * @return gpu
     */
    public double getGpu() {
        return gpu;
    }

    /**
     * Getter.
     *
     * @return memory
     */
    public double getMem() {
        return mem;
    }
    
}
