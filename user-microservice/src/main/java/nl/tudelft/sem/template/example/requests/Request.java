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

    /**
     * Constructor for Request class, that is sent by  user
     *  and will be stored in database.
     *
     * @param user - netId of user
     * @param description - description of request
     * @param cpu - amount of cpu power requested
     * @param gpu - amount of gpu power requested
     * @param mem - amount of memory requested
     * @param date - due date/deadline
     */
    public Request(String user, String description, double cpu, double gpu, double mem, Date date) {
        this.user = user;
        this.description = description;
        this.cpu = cpu;
        this.gpu = gpu;
        this.mem = mem;
        this.date = date;
    }

    public Request(){}

}
