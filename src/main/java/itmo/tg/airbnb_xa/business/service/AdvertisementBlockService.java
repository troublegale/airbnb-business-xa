package itmo.tg.airbnb_xa.business.service;

import itmo.tg.airbnb_xa.business.dto.AdvertisementBlockDTO;
import itmo.tg.airbnb_xa.business.exception.exceptions.NotAllowedException;
import itmo.tg.airbnb_xa.business.misc.ModelDTOConverter;
import itmo.tg.airbnb_xa.business.repository.main.AdvertisementBlockRepository;
import itmo.tg.airbnb_xa.business.repository.main.AdvertisementRepository;
import itmo.tg.airbnb_xa.security.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdvertisementBlockService {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementBlockRepository advertisementBlockRepository;

    public List<AdvertisementBlockDTO> getForAdvert(Long id, Integer page, Integer pageSize, User host) {
        var advert = advertisementRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Advertisement #" + id + " not found"));
        if (!advert.getHost().equals(host)) {
            throw new NotAllowedException("You do not own advertisement " + id);
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        var blocks = advertisementBlockRepository.findByAdvertisement(advert, pageable).getContent();
        return ModelDTOConverter.toAdvertisementBlockDTOList(blocks);
    }

}
