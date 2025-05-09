package itmo.tg.airbnb_xa.business.repository;

import itmo.tg.airbnb_xa.business.model.Fine;
import itmo.tg.airbnb_xa.business.model.enums.FineReason;
import itmo.tg.airbnb_xa.business.model.enums.FineStatus;
import itmo.tg.airbnb_xa.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    Page<Fine> findByUser(User user, Pageable pageable);

    Page<Fine> findByStatus(FineStatus status, Pageable pageable);

    Page<Fine> findByUserAndStatus(User user, FineStatus status, Pageable pageable);

    Fine findByTicketIdAndFineReason(Long ticketId, FineReason reason);

}
