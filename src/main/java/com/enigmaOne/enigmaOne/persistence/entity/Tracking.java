package com.enigmaOne.enigmaOne.persistence.entity;


import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners({AuditingEntityListener.class})
@Entity(name = "TRACKINGS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tracking  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackingState trackingState = TrackingState.PEDIDO;

    //Relaciones

    private Long orderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId",referencedColumnName = "id",insertable = false,updatable = false)
    @JsonIgnore
    private Order order;
}
