package itmo.tg.airbnb_xa.business.service;

import itmo.tg.airbnb_xa.business.dto.BookingRequestDTO;
import itmo.tg.airbnb_xa.business.dto.BookingResponseDTO;
import itmo.tg.airbnb_xa.business.exception.exceptions.*;
import itmo.tg.airbnb_xa.business.misc.ModelDTOConverter;
import itmo.tg.airbnb_xa.business.model.Booking;
import itmo.tg.airbnb_xa.business.model.enums.AdvertisementStatus;
import itmo.tg.airbnb_xa.business.model.enums.BookingStatus;
import itmo.tg.airbnb_xa.business.model.enums.FineReason;
import itmo.tg.airbnb_xa.business.repository.AdvertisementBlockRepository;
import itmo.tg.airbnb_xa.business.repository.AdvertisementRepository;
import itmo.tg.airbnb_xa.business.repository.BookingRepository;
import itmo.tg.airbnb_xa.security.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementBlockRepository advertisementBlockRepository;
    private final BookingRepository bookingRepository;

    private final PenaltyService penaltyService;

    public BookingResponseDTO get(Long id) {
        var booking = bookingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Booking #" + id + " not found"));
        return ModelDTOConverter.convert(booking);
    }

    public List<BookingResponseDTO> getAll(Integer page, Integer pageSize, Boolean active) {
        List<Booking> bookings;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (active) {
            bookings = bookingRepository.findByStatus(BookingStatus.ACTIVE, pageable).getContent();
        } else {
            bookings = bookingRepository.findAll(pageable).getContent();
        }
        return ModelDTOConverter.toBookingDTOList(bookings);
    }

    public List<BookingResponseDTO> getOwned(User guest, Integer page, Integer pageSize, Boolean active) {
        List<Booking> bookings;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (active) {
            bookings = bookingRepository.findByGuestAndStatus(guest, BookingStatus.ACTIVE, pageable).getContent();
        } else {
            bookings = bookingRepository.findByGuest(guest, pageable).getContent();
        }
        return ModelDTOConverter.toBookingDTOList(bookings);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BookingResponseDTO create(BookingRequestDTO dto, User guest) {

        verifyDates(dto.getStartDate(), dto.getEndDate());

        var advertId = dto.getAdvertisementId();
        var advert = advertisementRepository.findById(advertId).orElseThrow(() ->
                new NoSuchElementException("Advertisement #" + advertId + " not found"));

        if (advert.getHost().equals(guest)) {
            throw new BookOwnAdvertisementException("You can't book your own advertisement");
        }

        if (advert.getStatus() == AdvertisementStatus.BLOCKED) {
            var blocks = advertisementBlockRepository.findByAdvertisement(advert);
            assert !blocks.isEmpty();
            blocks.sort((b1, b2) -> {
                if (b1.getDateUntil().isAfter(b2.getDateUntil())) {
                    return -1;
                } else if (b1.getDateUntil().isBefore(b2.getDateUntil())) {
                    return 1;
                }
                return 0;
            });
            var until = blocks.get(0).getDateUntil();
            throw new AdvertisementBlockedException("Advertisement #" + advertId + " is blocked until " + until);
        }

        var activeBookings = bookingRepository.findByAdvertisementAndStatus(advert, BookingStatus.ACTIVE);
        activeBookings.forEach(booking ->
                verifyDatesConflict(booking.getStartDate(), booking.getEndDate(), dto.getStartDate(), dto.getEndDate())
        );

        var booking = ModelDTOConverter.convert(dto, advert, guest);
        booking.setStatus(BookingStatus.ACTIVE);
        bookingRepository.save(booking);
        log.info("Created booking #{}", booking.getId());
        return ModelDTOConverter.convert(booking);

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String cancel(Long id, User user) {

        var booking = bookingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Booking #" + id + " not found"));

        if (!booking.getGuest().equals(user) && !booking.getAdvertisement().getHost().equals(user)) {
            throw new NotAllowedException("Not allowed to cancel booking #" + id);
        }

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new NotAllowedException("Booking #" + id + " is already cancelled or expired");
        }

        if (booking.getGuest().equals(user)) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            log.info("Booking #{} cancelled by guest", booking.getId());
            return "You cancelled booking #" + id + " as a guest";
        }

        var advert = booking.getAdvertisement();
        penaltyService.blockAndAssignFine(advert, -1L, FineReason.SELF,
                LocalDate.now(), booking.getStartDate(), booking.getEndDate(), user);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking #{} cancelled by host", booking.getId());
        return "You cancelled booking #" + id + " as a host.\n" +
                "You were assigned with a fine. Refer to /fines/my\n" +
                "Your advertisement #" + advert.getId() + " is blocked for booking until " + booking.getEndDate();

    }

    private void verifyDates(LocalDate start, LocalDate end) {
        boolean cond1 = start.isBefore(end) || start.isEqual(end);
        boolean cond2 = start.isAfter(LocalDate.now()) || start.isEqual(LocalDate.now());
        if (!cond1 || !cond2) {
            throw new InvalidBookingDatesException("Booking dates are incorrect");
        }
    }

    private void verifyDatesConflict(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        boolean cond1 = start2.isAfter(end1);
        boolean cond2 = start1.isAfter(end2);
        if (!cond1 && !cond2) {
            throw new BookingDatesConflictException("Another booking already exists in the same period");
        }
    }

}
