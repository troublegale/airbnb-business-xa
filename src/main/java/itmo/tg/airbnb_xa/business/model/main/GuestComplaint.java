package itmo.tg.airbnb_xa.business.model.main;

import itmo.tg.airbnb_xa.business.model.enums.TicketStatus;
import itmo.tg.airbnb_xa.security.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "guest_complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "guest_id", nullable = false)
    @ManyToOne
    private User guest;

    @JoinColumn(name = "advertisement_id", nullable = false)
    @ManyToOne
    private Advertisement advertisement;

    @JoinColumn(name = "booking_id", nullable = false)
    @ManyToOne
    private Booking booking;

    @Column(name = "proof_link", nullable = false)
    private String proofLink;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @JoinColumn(name = "resolver_id")
    @ManyToOne
    private User resolver;

}
