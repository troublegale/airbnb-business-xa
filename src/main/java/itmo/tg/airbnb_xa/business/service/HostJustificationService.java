package itmo.tg.airbnb_xa.business.service;

import itmo.tg.airbnb_xa.business.dto.HostJustificationRequestDTO;
import itmo.tg.airbnb_xa.business.dto.HostJustificationResponseDTO;
import itmo.tg.airbnb_xa.business.exception.exceptions.NotAllowedException;
import itmo.tg.airbnb_xa.business.exception.exceptions.TicketAlreadyPublishedException;
import itmo.tg.airbnb_xa.business.exception.exceptions.TicketAlreadyResolvedException;
import itmo.tg.airbnb_xa.business.misc.ModelDTOConverter;
import itmo.tg.airbnb_xa.business.model.GuestComplaint;
import itmo.tg.airbnb_xa.business.model.HostJustification;
import itmo.tg.airbnb_xa.business.model.enums.FineReason;
import itmo.tg.airbnb_xa.business.model.enums.TicketStatus;
import itmo.tg.airbnb_xa.business.repository.GuestComplaintRepository;
import itmo.tg.airbnb_xa.business.repository.HostJustificationRepository;
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
public class HostJustificationService {

    private final HostJustificationRepository hostJustificationRepository;
    private final GuestComplaintRepository guestComplaintRepository;
    private final PenaltyService penaltyService;

    public HostJustificationResponseDTO get(Long id) {
        var ticket = hostJustificationRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Justification #" + id + " not found"));
        return ModelDTOConverter.convert(ticket);
    }

    public List<HostJustificationResponseDTO> getList(Integer page, Integer pageSize, String filter) {
        List<HostJustification> justifications;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (filter.equalsIgnoreCase("pending")) {
            justifications = hostJustificationRepository.findByStatus(TicketStatus.PENDING, pageable).getContent();
        } else if (filter.equalsIgnoreCase("resolved")) {
            justifications = hostJustificationRepository.findAll(pageable).stream().filter(
                    j -> j.getResolver() != null).toList();
        } else {
            justifications = hostJustificationRepository.findAll(pageable).getContent();
        }
        return ModelDTOConverter.toHostJustificationDTOList(justifications);
    }

    public List<HostJustificationResponseDTO> getOwned(User host, Integer page, Integer pageSize, String filter) {
        List<HostJustification> justifications;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (filter.equalsIgnoreCase("pending")) {
            justifications = hostJustificationRepository.findByHostAndStatus(host, TicketStatus.PENDING, pageable).getContent();
        } else if (filter.equalsIgnoreCase("resolved")) {
            justifications = hostJustificationRepository.findByHost(host, pageable).stream().filter(
                    j -> j.getResolver() != null).toList();
        } else {
            justifications = hostJustificationRepository.findByHost(host, pageable).getContent();
        }
        return ModelDTOConverter.toHostJustificationDTOList(justifications);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public HostJustificationResponseDTO create(HostJustificationRequestDTO dto, User host) {
        var complaintId = dto.getGuestComplaintId();
        var complaint = guestComplaintRepository.findById(complaintId).orElseThrow(() ->
                new NoSuchElementException("Complaint #" + complaintId + " not found"));
        verifyJustification(complaint, host);
        var justification = HostJustification.builder()
                .host(host)
                .complaint(complaint)
                .proofLink(dto.getProofLink())
                .status(TicketStatus.PENDING)
                .build();
        hostJustificationRepository.save(justification);
        log.info("Created host justification #{}", justification.getId());
        return ModelDTOConverter.convert(justification);
    }

    private void verifyJustification(GuestComplaint complaint, User host) {
        var advert = complaint.getAdvertisement();
        if (!advert.getHost().equals(host)) {
            throw new NotAllowedException(
                    "You do not own advertisement #" + advert.getId() + " mentioned in complaint #" + complaint.getId());
        }
        var exists = hostJustificationRepository
                .existsByComplaintAndHostAndStatusNot(complaint, host, TicketStatus.REJECTED);
        if (exists) {
            throw new TicketAlreadyPublishedException(
                    "Your justification on complaint #" + complaint.getId() + " is already approved or is still pending");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public HostJustificationResponseDTO approve(Long id, User resolver) {
        var ticket = hostJustificationRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Damage complaint #" + id + " not found"));
        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new TicketAlreadyResolvedException("Damage complaint #" + id + " is already resolved");
        }
        ticket.setStatus(TicketStatus.APPROVED);
        ticket.setResolver(resolver);
        hostJustificationRepository.save(ticket);
        log.info("Approved host justification #{}", ticket.getId());
        var booking = ticket.getComplaint().getBooking();
        var advert = booking.getAdvertisement();
        penaltyService.retractPenalty(advert, booking.getEndDate(), ticket.getComplaint().getId(), FineReason.GUEST);
        return ModelDTOConverter.convert(ticket);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public HostJustificationResponseDTO reject(Long id, User resolver) {
        var ticket = hostJustificationRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Damage complaint #" + id + " not found"));
        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new TicketAlreadyResolvedException("Damage complaint #" + id + " is already resolved");
        }
        ticket.setStatus(TicketStatus.REJECTED);
        ticket.setResolver(resolver);
        hostJustificationRepository.save(ticket);
        log.info("Rejected host justification #{}", ticket.getId());
        return ModelDTOConverter.convert(ticket);
    }

}
