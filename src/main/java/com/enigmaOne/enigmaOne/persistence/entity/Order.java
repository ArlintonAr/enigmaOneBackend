package com.enigmaOne.enigmaOne.persistence.entity;

import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;
import com.enigmaOne.enigmaOne.persistence.types.TypeOrder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

@EntityListeners({AuditingEntityListener.class})
@Entity(name = "ORDERS")
@Data
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

    //Stock
    private Long stockId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="stockId",referencedColumnName = "id",insertable = false,updatable = false)
    @JsonIgnore
    private Stock stock;

}
