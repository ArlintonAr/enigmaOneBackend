package com.enigmaOne.enigmaOne.persistence.entity;

import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;
import com.enigmaOne.enigmaOne.persistence.types.TypeOrder;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

@EntityListeners({AuditingEntityListener.class})
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity(name = "ORDERS")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "employee")
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOrder type =TypeOrder.MATERIAL;

    private Date estimatedDateStock;

    //Relaciones
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId",referencedColumnName = "id",insertable = false,updatable = false)
    @JsonIgnore
    private Employee employee;


    //Relaciones con materiales o servicios y el seguimiento
    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<ServiceOrder> serviceOrders;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<MaterialOrder> materialOrders;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Tracking> trackings;

    // Añadido: relación con OrderApproval. Cascade y orphanRemoval permiten eliminar ordenes sin romper FK.
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OrderApproval> orderApprovals;

    //Stock
    private Long stockId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="stockId",referencedColumnName = "id",insertable = false,updatable = false)
    @JsonIgnore
    private Stock stock;

    // Resumen del estado de aprobaciones de la orden
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDIENTE;

    // Estado actual del tracking (útil para UI: PEDIDO, APROBADO, RUTA, ALMACEN, RECHAZADO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackingState currentTrackingState =TrackingState.PEDIDO;

}
