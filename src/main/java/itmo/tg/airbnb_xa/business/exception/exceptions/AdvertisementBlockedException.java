package itmo.tg.airbnb_xa.business.exception.exceptions;

public class AdvertisementBlockedException extends RuntimeException {
    public AdvertisementBlockedException(String message) {
        super(message);
    }
}
