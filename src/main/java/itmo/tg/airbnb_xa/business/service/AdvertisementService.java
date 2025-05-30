package itmo.tg.airbnb_xa.business.service;

import itmo.tg.airbnb_xa.business.dto.AdvertisementRequestDTO;
import itmo.tg.airbnb_xa.business.dto.AdvertisementResponseDTO;
import itmo.tg.airbnb_xa.business.dto.BookingResponseDTO;
import itmo.tg.airbnb_xa.business.exception.exceptions.NotAllowedException;
import itmo.tg.airbnb_xa.business.exception.exceptions.TransactionException;
import itmo.tg.airbnb_xa.business.misc.ModelDTOConverter;
import itmo.tg.airbnb_xa.business.model.main.Advertisement;
import itmo.tg.airbnb_xa.business.model.main.Booking;
import itmo.tg.airbnb_xa.business.model.enums.AdvertisementStatus;
import itmo.tg.airbnb_xa.business.model.enums.BookingStatus;
import itmo.tg.airbnb_xa.business.repository.main.AdvertisementRepository;
import itmo.tg.airbnb_xa.business.repository.main.BookingRepository;
import itmo.tg.airbnb_xa.security.model.Role;
import itmo.tg.airbnb_xa.security.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final BookingRepository bookingRepository;

    private final JtaTransactionManager jtaTransactionManager;

    public AdvertisementResponseDTO get(Long id) {
        var advert = advertisementRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Advertisement #" + id + " not found"));
        return ModelDTOConverter.convert(advert);
    }

    public List<AdvertisementResponseDTO> getAll(Integer page, Integer pageSize, Boolean active) {
        List<Advertisement> adverts;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (active) {
            adverts = advertisementRepository.findByStatus(AdvertisementStatus.ACTIVE, pageable).getContent();
        } else {
            adverts = advertisementRepository.findAll(pageable).getContent();
        }
        return ModelDTOConverter.toAdvertisementDTOList(adverts);
    }

    public List<BookingResponseDTO> getBookings(Long id, Integer page, Integer pageSize, Boolean active) {
        var advert = advertisementRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Advertisement #" + id + " not found"));
        List<Booking> bookings;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (active) {
            bookings = bookingRepository.findByAdvertisementAndStatus(advert, BookingStatus.ACTIVE, pageable).getContent();
        } else {
            bookings = bookingRepository.findByAdvertisement(advert, pageable).getContent();
        }
        return ModelDTOConverter.toBookingDTOList(bookings);
    }

    public List<AdvertisementResponseDTO> getOwned(User host, Integer page, Integer pageSize, Boolean active) {
        List<Advertisement> adverts;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (active) {
            adverts = advertisementRepository.findByHostAndStatus(host, AdvertisementStatus.ACTIVE, pageable).getContent();
        } else {
            adverts = advertisementRepository.findByHost(host, pageable).getContent();
        }
        return ModelDTOConverter.toAdvertisementDTOList(adverts);
    }
    
    public AdvertisementResponseDTO create(AdvertisementRequestDTO dto, User host) {
        var advert = ModelDTOConverter.convert(dto, host);
        advert.setStatus(AdvertisementStatus.ACTIVE);
        advertisementRepository.save(advert);
        log.info("Created advertisement #{}", advert.getId());
        return ModelDTOConverter.convert(advert);
    }

    public AdvertisementResponseDTO update(Long id, AdvertisementRequestDTO dto, User host) {
        try {
            var userTransaction = jtaTransactionManager.getUserTransaction();
            userTransaction.begin();

            var advert = advertisementRepository.findById(id).orElseThrow(() ->
                    new NoSuchElementException("Advertisement #" + id + " not found"));
            if (advert.getHost().equals(host) || host.getRole() == Role.ROLE_ADMIN) {
                advert.setAddress(dto.getAddress());
                advert.setRooms(dto.getRooms());
                advert.setBookPrice(dto.getBookPrice());
                advert.setPricePerNight(dto.getPricePerNight());
                advertisementRepository.save(advert);

                userTransaction.commit();

                log.info("Updated advertisement #{}", advert.getId());
                return ModelDTOConverter.convert(advert);
            }
            throw new NotAllowedException("Not allowed to update advertisement #" + id);
        } catch (NoSuchElementException | NotAllowedException e) {
            rollbackSafely();
            throw e;
        } catch (Exception e) {
            rollbackSafely();
            throw new TransactionException("Transaction failed in cancel (booking)");
        }
    }

    private void rollbackSafely() {
        try {
            var userTransaction = jtaTransactionManager.getUserTransaction();
            userTransaction.rollback();
        } catch (Exception rollbackEx) {
            log.error("Rollback failed", rollbackEx);
        }
    }

}
