package itmo.tg.airbnb_xa.business.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.tg.airbnb_xa.business.dto.GuestComplaintResponseDTO;
import itmo.tg.airbnb_xa.business.dto.HostDamageComplaintResponseDTO;
import itmo.tg.airbnb_xa.business.dto.HostJustificationResponseDTO;
import itmo.tg.airbnb_xa.business.service.GuestComplaintService;
import itmo.tg.airbnb_xa.business.service.HostDamageComplaintService;
import itmo.tg.airbnb_xa.business.service.HostJustificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
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
        if (type.startsWith("Guest Complaint")) {
            var dto = objectMapper.readValue(payload, GuestComplaintResponseDTO.class);
            dto = guestComplaintService.updateViaJira(dto.getId(), dto.getStatus());
            guestComplaintKafkaTemplate.send("guest-complaints", dto);
        } else if (type.startsWith("Host Damage Complaint")) {
            var dto = objectMapper.readValue(payload, HostDamageComplaintResponseDTO.class);
            dto = hostDamageComplaintService.updateViaJira(dto.getId(), dto.getStatus());
            hostDamageComplaintKafkaTemplate.send("host-damage-complaints", dto);
        } else {
            var dto = objectMapper.readValue(payload, HostJustificationResponseDTO.class);
            dto = hostJustificationService.updateViaJira(dto.getId(), dto.getStatus());
            hostJustificationKafkaTemplate.send("host-justifications", dto);
        }
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

}
