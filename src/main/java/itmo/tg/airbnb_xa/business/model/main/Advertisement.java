package itmo.tg.airbnb_xa.business.model.main;

import itmo.tg.airbnb_xa.business.model.enums.AdvertisementStatus;
import itmo.tg.airbnb_xa.security.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "advertisements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "rooms", nullable = false)
    private int rooms;

    @Column(name = "book_price", nullable = false)
    private int bookPrice;

    @Column(name = "price_per_night", nullable = false)
    private int pricePerNight;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdvertisementStatus status;

    @JoinColumn(name = "host_id", nullable = false)
    @ManyToOne
    private User host;

}
