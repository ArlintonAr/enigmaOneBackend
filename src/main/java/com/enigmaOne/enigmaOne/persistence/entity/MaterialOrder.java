package com.enigmaOne.enigmaOne.persistence.entity;


import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import com.enigmaOne.enigmaOne.persistence.types.TypeMaterial;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Date;

@EntityListeners({AuditingEntityListener.class})
@Entity(name = "MATERIALS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MaterialOrder extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMaterial typeMaterial =TypeMaterial.ACTIVOS;

    @Column(unique = true,nullable = false)
    private String code;
    private Integer quantity;
    private String unitOfMeasure;
    private String characteristics;

    @Column(nullable = false)
    private LocalDate estimatedDateStock;

    private String observations;
    private String photo;

    //Relaciones
    private Long orderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="orderId",referencedColumnName = "id",insertable = false,updatable = false)
    @JsonIgnore
    private Order order;


}
