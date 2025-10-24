package com.enigmaOne.enigmaOne.persistence.entity;

import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners({AuditingEntityListener.class})
@Table(name = "DETAIL_ENTRY_MATERIALS")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DetailEntryMaterial extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destinationMaterial;

    @Column(nullable = false)
    private String transactionCode;

    @Column(nullable = false)
    private String employeeNameRequerter;

    @Column(nullable = false)
    private String employeeNameAuthorized;

    //Relaciones
    private Long movementId; //id del movimiento al que pertenece el detalle

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movementId", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Movement movement; // Movimiento asociado al detalle de salida del material

    // Nuevo: referencia al stock (producto) y cantidad — permite que un movimiento tenga varios productos
    private Long stockId; // id del stock/producto al que corresponde este detalle

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stockId", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Stock stock; // Stock asociado a este detalle

    @Column(nullable = false)
    private Integer quantity; // cantidad del stock en este detalle

}
