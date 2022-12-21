package nl.tudelft.sem.template.schedule.domain.request;

/**
 * A DDD domain event that indicated a request was scheduled.
 */
public class RequestWasScheduledEvent {
    private final ScheduledRequest request;

    public RequestWasScheduledEvent(ScheduledRequest request) {
        this.request = request;
    }

    public ScheduledRequest getRequest() {
        return this.request;
    }
}
