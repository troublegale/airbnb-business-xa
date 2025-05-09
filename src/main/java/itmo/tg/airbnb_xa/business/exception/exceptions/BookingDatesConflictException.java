package itmo.tg.airbnb_xa.business.exception.exceptions;

public class BookingDatesConflictException extends RuntimeException {
    public BookingDatesConflictException(String message) {
        super(message);
    }
}
