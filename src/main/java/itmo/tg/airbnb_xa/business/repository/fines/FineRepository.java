package itmo.tg.airbnb_xa.business.repository.fines;

import itmo.tg.airbnb_xa.business.model.fines.Fine;
import itmo.tg.airbnb_xa.business.model.enums.FineReason;
import itmo.tg.airbnb_xa.business.model.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    Page<Fine> findByEmail(String email, Pageable pageable);

    Page<Fine> findByStatus(FineStatus status, Pageable pageable);

    Page<Fine> findByEmailAndStatus(String email, FineStatus status, Pageable pageable);

    Fine findByTicketIdAndFineReason(Long ticketId, FineReason reason);

}
