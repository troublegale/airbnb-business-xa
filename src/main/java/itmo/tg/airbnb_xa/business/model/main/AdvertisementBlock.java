package itmo.tg.airbnb_xa.business.model.main;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "advertisement_blocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "advertisement_id", nullable = false)
    @ManyToOne
    private Advertisement advertisement;

    @Column(name = "date_until", nullable = false)
    private LocalDate dateUntil;

}
