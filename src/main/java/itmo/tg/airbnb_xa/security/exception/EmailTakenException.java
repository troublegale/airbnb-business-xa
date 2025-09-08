package itmo.tg.airbnb_xa.security.exception;

public class EmailTakenException extends RuntimeException {

    public EmailTakenException(String message) {
        super(message);
    }

}
