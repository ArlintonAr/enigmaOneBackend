package com.enigmaOne.enigmaOne.persistence.entity;


import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import com.enigmaOne.enigmaOne.persistence.types.AccordingType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

@EntityListeners( {AuditingEntityListener.class})
@Entity
@Table(name = "STOCKS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stock extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique =true)
    private String code;
    @Column(nullable = false)
    private Integer quantity;
    private String unitOfMeasure;
    private String description;
    private String characteristics;
    private Date entryDate;

    @Enumerated(EnumType.STRING)
    private AccordingType accordingType = AccordingType.SI;

    private String messageAccordingType;
    private String photo;

    //Relaciones

    // Nota: eliminada la relación directa con Movement. Un stock puede aparecer en muchos detalles de movimientos.

    // Detalles de entrada que referencian este stock
    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DetailEntryMaterial> detailEntryMaterials;

    // Detalles de salida que referencian este stock
    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DetailExitMaterial> detailExitMaterials;

    //Almacen
    private  Long warehouseId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouseId", referencedColumnName = "id", insertable = false, updatable = false)
    private Warehouse warehouse;

    //Ordenes
    private Long orderId;

    @OneToMany(mappedBy = "stock",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Order> orders;




}
