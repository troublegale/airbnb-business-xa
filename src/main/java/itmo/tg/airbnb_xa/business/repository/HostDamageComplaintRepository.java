package itmo.tg.airbnb_xa.business.repository;

import itmo.tg.airbnb_xa.business.model.Booking;
import itmo.tg.airbnb_xa.business.model.HostDamageComplaint;
import itmo.tg.airbnb_xa.business.model.enums.TicketStatus;
import itmo.tg.airbnb_xa.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostDamageComplaintRepository extends JpaRepository<HostDamageComplaint, Long> {

    Page<HostDamageComplaint> findByStatus(TicketStatus status, Pageable pageable);

    Page<HostDamageComplaint> findByHost(User host, Pageable pageable);

    Page<HostDamageComplaint> findByHostAndStatus(User host, TicketStatus status, Pageable pageable);

    Boolean existsByBookingAndHostAndStatusNot(Booking booking, User host, TicketStatus status);

}
