package com.enigmaOne.enigmaOne.persistence.entity;

import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import com.enigmaOne.enigmaOne.persistence.types.MovementType;
import com.enigmaOne.enigmaOne.persistence.types.ReturnableType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

@EntityListeners({AuditingEntityListener.class}) // Este listener permite que la entidad sea auditada automáticamente con fecha de creación y actualización
@Table(name = "MOVEMENTS")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Movement extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type =MovementType.RETORNABLE ;


    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnableType returnable = ReturnableType.SALIDA;

    @Column(nullable = false,unique = true)
    private String transactionCode;

    @Column(nullable = false)
    private Long materialRequesterId;

    private String materialRequerterFirstName;
    private String materialRequerterLastName;

    private Date returnDate;

    //Relaciones
    private Long employeeId; //id del empleado que realiza el movimiento y autoriza

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore // Evita la serialización infinita
    private Employee employee;


    @OneToMany(mappedBy = "movement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DetailEntryMaterial> detailEntryMaterials;


    @OneToMany(mappedBy = "movement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DetailExitMaterial> detailExitMaterials;


}
