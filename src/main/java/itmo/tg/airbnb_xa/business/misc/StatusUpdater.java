package itmo.tg.airbnb_xa.business.misc;

import itmo.tg.airbnb_xa.business.model.enums.AdvertisementStatus;
import itmo.tg.airbnb_xa.business.model.enums.BookingStatus;
import itmo.tg.airbnb_xa.business.repository.main.AdvertisementBlockRepository;
import itmo.tg.airbnb_xa.business.repository.main.AdvertisementRepository;
import itmo.tg.airbnb_xa.business.repository.main.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@EnableAsync
@Component
@RequiredArgsConstructor
@Slf4j
public class StatusUpdater {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementBlockRepository advertisementBlockRepository;
    private final BookingRepository bookingRepository;

    @Async
    @Scheduled(fixedRate = 1000 * 60 * 60, initialDelay = 1000 * 60)
    public void statusUpdateScheduled() {

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

        log.info("Finished scheduled scanning");
    }

}
