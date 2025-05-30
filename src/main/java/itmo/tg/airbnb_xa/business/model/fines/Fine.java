package itmo.tg.airbnb_xa.business.model.fines;

import itmo.tg.airbnb_xa.business.model.enums.FineReason;
import itmo.tg.airbnb_xa.business.model.enums.FineStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username", nullable = false)
    private String username;

//    @JoinColumn(name = "user_id", nullable = false)
//    @ManyToOne
//    private User user;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FineStatus status;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "fine_reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private FineReason fineReason;

}
