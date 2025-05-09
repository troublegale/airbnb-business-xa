package itmo.tg.airbnb_xa.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestComplaintRequestDTO {

    @NotNull
    private Long bookingId;

    @NotNull
    @NotBlank
    private String proofLink;

}
