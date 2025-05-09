package itmo.tg.airbnb_xa.business.misc;

import itmo.tg.airbnb_xa.business.model.enums.AdvertisementStatus;
import itmo.tg.airbnb_xa.business.model.enums.BookingStatus;
import itmo.tg.airbnb_xa.business.repository.AdvertisementBlockRepository;
import itmo.tg.airbnb_xa.business.repository.AdvertisementRepository;
import itmo.tg.airbnb_xa.business.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatusUpdater {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementBlockRepository advertisementBlockRepository;
    private final BookingRepository bookingRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void startStatusMonitoring() {
        var monitorThread = new Thread(this::doMonitoring);
        monitorThread.start();
        log.info("Started status monitoring thread");
    }

    private void doMonitoring() {
        while (true) {
            updateAndWait();
        }
    }

    private void updateAndWait() {

        log.info("Scanning for expired blocks & bookings");

        var blocks = advertisementBlockRepository.findAll();
        for (var block : blocks) {
            if (LocalDate.now().isAfter(block.getDateUntil())) {
                var advert = block.getAdvertisement();
                advert.setStatus(AdvertisementStatus.ACTIVE);
                advertisementRepository.save(advert);
                advertisementBlockRepository.delete(block);
            }
        }

        var bookings = bookingRepository.findByStatus(BookingStatus.ACTIVE);
        for (var booking : bookings) {
            if (LocalDate.now().isAfter(booking.getEndDate())) {
                booking.setStatus(BookingStatus.EXPIRED);
                bookingRepository.save(booking);
            }
        }

        try {
            Thread.sleep(1000 * 60 * 60);
        } catch (InterruptedException e) {
            log.warn("Interrupt in status monitoring thread: {}", e.getMessage());
        }

    }

}
