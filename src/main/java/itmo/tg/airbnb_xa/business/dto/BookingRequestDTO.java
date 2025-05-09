package itmo.tg.airbnb_xa.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {

    @NotNull
    private Long advertisementId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

}
