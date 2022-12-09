package nl.tudelft.sem.template.example.requests;

import java.util.Date;
import lombok.Data;

@Data
public class RequestData {
    private String user;
    private String description;
    private double cpu;
    private double gpu;
    private double memory;
    //private Date date;
}
