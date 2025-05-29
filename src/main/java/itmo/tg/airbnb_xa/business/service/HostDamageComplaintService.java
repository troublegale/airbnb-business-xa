package itmo.tg.airbnb_xa.business.service;

import itmo.tg.airbnb_xa.business.dto.HostDamageComplaintRequestDTO;
import itmo.tg.airbnb_xa.business.dto.HostDamageComplaintResponseDTO;
import itmo.tg.airbnb_xa.business.exception.exceptions.TicketAlreadyPublishedException;
import itmo.tg.airbnb_xa.business.exception.exceptions.TicketAlreadyResolvedException;
import itmo.tg.airbnb_xa.business.misc.ModelDTOConverter;
import itmo.tg.airbnb_xa.business.model.main.Booking;
import itmo.tg.airbnb_xa.business.model.main.HostDamageComplaint;
import itmo.tg.airbnb_xa.business.model.enums.FineReason;
import itmo.tg.airbnb_xa.business.model.enums.TicketStatus;
import itmo.tg.airbnb_xa.business.repository.main.BookingRepository;
import itmo.tg.airbnb_xa.business.repository.main.HostDamageComplaintRepository;
import itmo.tg.airbnb_xa.security.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class HostDamageComplaintService {

    private final HostDamageComplaintRepository hostDamageComplaintRepository;
    private final BookingRepository bookingRepository;
    private final PenaltyService penaltyService;

    public HostDamageComplaintResponseDTO get(Long id) {
        var ticket = hostDamageComplaintRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Damage complaint #" + id + " not found"));
        return ModelDTOConverter.convert(ticket);
    }

    public List<HostDamageComplaintResponseDTO> getList(Integer page, Integer pageSize, String filter) {
        List<HostDamageComplaint> complaints;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (filter.equalsIgnoreCase("pending")) {
            complaints = hostDamageComplaintRepository.findByStatus(TicketStatus.PENDING, pageable).getContent();
        } else if (filter.equalsIgnoreCase("resolved")) {
            complaints = hostDamageComplaintRepository.findAll(pageable).stream().filter(
                    c -> c.getResolver() != null).toList();
        } else {
            complaints = hostDamageComplaintRepository.findAll(pageable).getContent();
        }
        return ModelDTOConverter.toHostDamageComplaintDTOList(complaints);
    }

    public List<HostDamageComplaintResponseDTO> getOwned(User host, Integer page, Integer pageSize, String filter) {
        List<HostDamageComplaint> complaints;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (filter.equalsIgnoreCase("pending")) {
            complaints = hostDamageComplaintRepository.findByHostAndStatus(host, TicketStatus.PENDING, pageable).getContent();
        } else if (filter.equalsIgnoreCase("resolved")) {
            complaints = hostDamageComplaintRepository.findByHost(host, pageable).stream().filter(
                    c -> c.getResolver() != null).toList();
        } else {
            complaints = hostDamageComplaintRepository.findByHost(host, pageable).getContent();
        }
        return ModelDTOConverter.toHostDamageComplaintDTOList(complaints);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public HostDamageComplaintResponseDTO create(HostDamageComplaintRequestDTO dto, User host) {
        var bookingId = dto.getBookingId();
        var booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchElementException("Booking #" + bookingId + " not found"));
        verifyComplaint(booking, host);
        var complaint = HostDamageComplaint.builder()
                .host(host)
                .booking(booking)
                .proofLink(dto.getProofLink())
                .compensationAmount(dto.getCompensationAmount())
                .status(TicketStatus.PENDING)
                .build();
        hostDamageComplaintRepository.save(complaint);
        log.info("Created host damage complaint #{}", complaint.getId());
        return ModelDTOConverter.convert(complaint);
    }

    private void verifyComplaint(Booking booking, User host) {
        var exists = hostDamageComplaintRepository
                .existsByBookingAndHostAndStatusNot(booking, host, TicketStatus.REJECTED);
        if (exists) {
            throw new TicketAlreadyPublishedException(
                    "Your complaint on booking #" + booking.getId() + " is already approved or is still pending");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public HostDamageComplaintResponseDTO approve(Long id, User resolver) {
        var ticket = hostDamageComplaintRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Damage complaint #" + id + " not found"));
        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new TicketAlreadyResolvedException("Damage complaint #" + id + " is already resolved");
        }
        ticket.setStatus(TicketStatus.APPROVED);
        ticket.setResolver(resolver);
        hostDamageComplaintRepository.save(ticket);
        log.info("Approved host damage complaint #{}", ticket.getId());
        penaltyService.assignFine(ticket.getCompensationAmount(), ticket.getBooking().getGuest(),
                ticket.getId(), FineReason.DAMAGE);
        return ModelDTOConverter.convert(ticket);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public HostDamageComplaintResponseDTO reject(Long id, User resolver) {
        var ticket = hostDamageComplaintRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Damage complaint #" + id + " not found"));
        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new TicketAlreadyResolvedException("Damage complaint #" + id + " is already resolved");
        }
        ticket.setStatus(TicketStatus.REJECTED);
        ticket.setResolver(resolver);
        hostDamageComplaintRepository.save(ticket);
        log.info("Rejected host damage complaint #{}", ticket.getId());
        return ModelDTOConverter.convert(ticket);
    }

}
