package itmo.tg.airbnb_xa.business.model.main;

import itmo.tg.airbnb_xa.business.model.enums.TicketStatus;
import itmo.tg.airbnb_xa.security.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "host_justifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostJustification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "host_id", nullable = false)
    @ManyToOne
    private User host;

    @JoinColumn(name = "complaint_id", nullable = false)
    @ManyToOne
    private GuestComplaint complaint;

    @Column(name = "proof_link", nullable = false)
    private String proofLink;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @JoinColumn(name = "resolver_id")
    @ManyToOne
    private User resolver;

}
