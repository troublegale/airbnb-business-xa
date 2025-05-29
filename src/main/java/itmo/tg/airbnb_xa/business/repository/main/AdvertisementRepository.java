package itmo.tg.airbnb_xa.business.repository.main;

import itmo.tg.airbnb_xa.business.model.main.Advertisement;
import itmo.tg.airbnb_xa.business.model.enums.AdvertisementStatus;
import itmo.tg.airbnb_xa.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    Page<Advertisement> findByHost(User host, Pageable pageable);

    Page<Advertisement> findByStatus(AdvertisementStatus status, Pageable pageable);

    Page<Advertisement> findByHostAndStatus(User host, AdvertisementStatus status, Pageable pageable);

}
