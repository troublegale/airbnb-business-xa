package itmo.tg.airbnb_xa.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementRequestDTO {

    @NotNull
    @NotBlank
    private String address;

    @NotNull
    @Positive
    private Integer rooms;

    @NotNull
    @Positive
    private Integer bookPrice;

    @NotNull
    @Positive
    private Integer pricePerNight;

}
