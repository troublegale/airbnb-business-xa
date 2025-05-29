package itmo.tg.airbnb_xa.business.service;

import itmo.tg.airbnb_xa.business.model.main.Advertisement;
import itmo.tg.airbnb_xa.business.model.main.AdvertisementBlock;
import itmo.tg.airbnb_xa.business.model.fines.Fine;
import itmo.tg.airbnb_xa.business.model.enums.AdvertisementStatus;
import itmo.tg.airbnb_xa.business.model.enums.FineReason;
import itmo.tg.airbnb_xa.business.model.enums.FineStatus;
import itmo.tg.airbnb_xa.business.repository.main.AdvertisementBlockRepository;
import itmo.tg.airbnb_xa.business.repository.main.AdvertisementRepository;
import itmo.tg.airbnb_xa.business.repository.fines.FineRepository;
import itmo.tg.airbnb_xa.security.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaltyService {

    private final FineRepository fineRepository;
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementBlockRepository advertisementBlockRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void blockAndAssignFine(Advertisement advertisement, Long ticketId, FineReason fineReason,
                                   LocalDate assigningDate, LocalDate startDate, LocalDate endDate, User host) {
        var block = AdvertisementBlock.builder()
                .advertisement(advertisement)
                .dateUntil(endDate)
                .build();
        advertisementBlockRepository.save(block);
        log.info("Advertisement #{} received block #{}", advertisement.getId(), block.getId());
        if (advertisement.getStatus() != AdvertisementStatus.BLOCKED) {
            advertisement.setStatus(AdvertisementStatus.BLOCKED);
            advertisementRepository.save(advertisement);
        }

        var amount = calculateFineAmount(
                assigningDate, startDate, endDate, advertisement.getBookPrice(), advertisement.getPricePerNight());
        assignFine(amount, host, ticketId, fineReason);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void assignFine(Double amount, User user, Long ticketId, FineReason fineReason) {
        var fine = Fine.builder()
                .user(user)
                .amount(amount)
                .status(FineStatus.ACTIVE)
                .ticketId(ticketId)
                .fineReason(fineReason)
                .build();
        fineRepository.save(fine);
        log.info("User #{} received fine #{}", user.getId(), fine.getId());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void retractPenalty(Advertisement advertisement, LocalDate until, Long ticketId, FineReason fineReason) {
        var blocks = advertisementBlockRepository.findByAdvertisement(advertisement);
        var exactBlock = blocks.stream().filter(b -> b.getDateUntil().equals(until)).toList();
        var block = exactBlock.get(0);
        advertisementBlockRepository.delete(block);
        log.info("Block #{} removed from advertisement #{}", block.getId(), advertisement.getId());
        if (blocks.size() == 1) {
            advertisement.setStatus(AdvertisementStatus.ACTIVE);
            advertisementRepository.save(advertisement);
            log.info("Advertisement #{} is active", advertisement.getId());
        }

        var fine = fineRepository.findByTicketIdAndFineReason(ticketId, fineReason);
        fine.setStatus(FineStatus.CANCELLED);
        fineRepository.save(fine);
        log.info("Cancelled fine #{}", fine.getId());
    }

    private double calculateFineAmount(
            LocalDate assigningDate, LocalDate startDate, LocalDate endDate, Integer bookPrice, Integer pricePerNight) {
        double amount;
        long nights;
        if (assigningDate.isAfter(startDate)) {
            nights = ChronoUnit.DAYS.between(assigningDate, endDate) + 1;
            amount = nights * pricePerNight / 2.0;
        } else if (ChronoUnit.DAYS.between(assigningDate, startDate) <= 2) {
            nights = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            amount = nights * pricePerNight / 2.0;
        } else if (ChronoUnit.DAYS.between(assigningDate, startDate) <= 30) {
            amount = bookPrice / 4.0;
        } else {
            amount = bookPrice / 10.0;
        }
        if (amount < 50) {
            amount = 50;
        }
        return amount;
    }

}
