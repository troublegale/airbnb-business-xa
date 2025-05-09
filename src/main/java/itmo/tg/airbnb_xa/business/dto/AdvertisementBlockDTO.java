package itmo.tg.airbnb_xa.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementBlockDTO {


    private Long advertisementId;

    private LocalDate dateUntil;

}
