package itmo.tg.airbnb_xa.business.dto;

import itmo.tg.airbnb_xa.business.model.enums.AdvertisementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementResponseDTO {

    private Long id;

    private String address;

    private Integer rooms;

    private Integer bookPrice;

    private Integer pricePerNight;

    private AdvertisementStatus status;

    private String hostUsername;

}
