package itmo.tg.airbnb_xa.business.repository.main;

import itmo.tg.airbnb_xa.business.model.main.Advertisement;
import itmo.tg.airbnb_xa.business.model.main.AdvertisementBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementBlockRepository extends JpaRepository<AdvertisementBlock, Long> {

    List<AdvertisementBlock> findByAdvertisement(Advertisement advertisement);

    Page<AdvertisementBlock> findByAdvertisement(Advertisement advert, Pageable pageable);

}
