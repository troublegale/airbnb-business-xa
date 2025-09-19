package itmo.tg.airbnb_xa.business.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.tg.airbnb_xa.business.dto.GuestComplaintResponseDTO;
import itmo.tg.airbnb_xa.business.dto.HostDamageComplaintResponseDTO;
import itmo.tg.airbnb_xa.business.dto.HostJustificationResponseDTO;
import itmo.tg.airbnb_xa.business.model.enums.TicketStatus;
import itmo.tg.airbnb_xa.business.service.GuestComplaintService;
import itmo.tg.airbnb_xa.business.service.HostDamageComplaintService;
import itmo.tg.airbnb_xa.business.service.HostJustificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class JiraWebhookController {

    private final GuestComplaintService guestComplaintService;
    private final HostDamageComplaintService  hostDamageComplaintService;
    private final HostJustificationService  hostJustificationService;

    private final KafkaTemplate<String, GuestComplaintResponseDTO> guestComplaintKafkaTemplate;
    private final KafkaTemplate<String, HostDamageComplaintResponseDTO> hostDamageComplaintKafkaTemplate;
    private final KafkaTemplate<String, HostJustificationResponseDTO> hostJustificationKafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public void handleWebhook(@RequestBody String payload) throws JsonProcessingException {
        var node = objectMapper.readTree(payload);
        var type = node.get("issue").get("fields").get("summary").asText();
        var description = node.get("issue").get("fields").get("description").asText();
        var status = node.get("issue").get("fields").get("status").get("name").asText();
        System.out.println(type);
        System.out.println(description);
        System.out.println(status);
        if (type.startsWith("Guest Complaint")) {
            var dto = objectMapper.readValue(description, GuestComplaintResponseDTO.class);
            dto = guestComplaintService.updateViaJira(dto.getId(), TicketStatus.valueOf(status));
            guestComplaintKafkaTemplate.send("guest-complaints", dto);
        } else if (type.startsWith("Host Damage Complaint")) {
            var dto = objectMapper.readValue(description, HostDamageComplaintResponseDTO.class);
            dto = hostDamageComplaintService.updateViaJira(dto.getId(), TicketStatus.valueOf(status));
            hostDamageComplaintKafkaTemplate.send("host-damage-complaints", dto);
        } else {
            var dto = objectMapper.readValue(description, HostJustificationResponseDTO.class);
            dto = hostJustificationService.updateViaJira(dto.getId(), TicketStatus.valueOf(status));
            hostJustificationKafkaTemplate.send("host-justifications", dto);
        }
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

}
