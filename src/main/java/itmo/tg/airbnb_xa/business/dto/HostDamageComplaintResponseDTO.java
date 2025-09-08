package itmo.tg.airbnb_xa.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import itmo.tg.airbnb_xa.business.model.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HostDamageComplaintResponseDTO {

    private Long id;

    private String hostEmail;

    private Long bookingId;

    private String proofLink;

    private Double compensationAmount;

    private TicketStatus status;

    private String resolverEmail;

}
