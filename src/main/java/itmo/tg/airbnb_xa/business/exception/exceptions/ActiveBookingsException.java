package itmo.tg.airbnb_xa.business.exception.exceptions;

public class ActiveBookingsException extends RuntimeException {
    public ActiveBookingsException(String message) {
        super(message);
    }
}
