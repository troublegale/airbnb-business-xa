package itmo.tg.airbnb_xa.business.exception.exceptions;

public class TransactionException extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }
}
