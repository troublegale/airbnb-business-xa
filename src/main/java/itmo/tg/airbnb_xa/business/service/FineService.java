package itmo.tg.airbnb_xa.business.service;

import itmo.tg.airbnb_xa.business.dto.FineDTO;
import itmo.tg.airbnb_xa.business.misc.ModelDTOConverter;
import itmo.tg.airbnb_xa.business.model.fines.Fine;
import itmo.tg.airbnb_xa.business.model.enums.FineStatus;
import itmo.tg.airbnb_xa.business.repository.fines.FineRepository;
import itmo.tg.airbnb_xa.security.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FineService {

    private final FineRepository fineRepository;

    public List<FineDTO> getAll(Integer page, Integer pageSize, Boolean active) {
        List<Fine> fines;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (active) {
            fines = fineRepository.findByStatus(FineStatus.ACTIVE, pageable).getContent();
        } else {
            fines = fineRepository.findAll(pageable).getContent();
        }
        return ModelDTOConverter.toFineDTOList(fines);
    }

    public List<FineDTO> getAssignedTo(User user, Integer page, Integer pageSize, Boolean active) {
        List<Fine> fines;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id"));
        if (active) {
            fines = fineRepository.findByUsernameAndStatus(user.getUsername(), FineStatus.ACTIVE, pageable).getContent();
        } else {
            fines = fineRepository.findByUsername(user.getUsername(), pageable).getContent();
        }
        return ModelDTOConverter.toFineDTOList(fines);
    }

}
