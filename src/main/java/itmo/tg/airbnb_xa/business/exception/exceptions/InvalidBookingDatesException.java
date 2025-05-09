package itmo.tg.airbnb_xa.business.exception.exceptions;

public class InvalidBookingDatesException extends RuntimeException {
    public InvalidBookingDatesException(String message) {
        super(message);
    }
}
