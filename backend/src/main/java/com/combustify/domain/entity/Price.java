package com.combustify.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedAt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prices", indexes = {
    @Index(name = "idx_station_fuel", columnList = "station_id, fuel_type"),
    @Index(name = "idx_reported_at", columnList = "reported_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private GasStation station;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @Column(name = "verification_count")
    private Integer verificationCount;

    @CreatedAt
    @Column(name = "reported_at", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();
        this.verificationCount = 0;
    }

    public enum FuelType {
        GASOLINA, DIESEL, ETANOL, GNV
    }

}
