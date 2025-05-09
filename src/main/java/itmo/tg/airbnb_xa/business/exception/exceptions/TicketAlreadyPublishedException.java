package itmo.tg.airbnb_xa.business.exception.exceptions;

public class TicketAlreadyPublishedException extends RuntimeException {
    public TicketAlreadyPublishedException(String message) {
        super(message);
    }
}
