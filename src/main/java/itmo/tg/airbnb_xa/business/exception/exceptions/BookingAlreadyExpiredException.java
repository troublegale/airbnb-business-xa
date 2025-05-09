package itmo.tg.airbnb_xa.business.exception.exceptions;

public class BookingAlreadyExpiredException extends RuntimeException {
    public BookingAlreadyExpiredException(String message) {
        super(message);
    }
}
