package com.enigmaOne.enigmaOne.persistence.entity;

import com.enigmaOne.enigmaOne.persistence.audit.AuditEmployeeListener;
import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;


@EntityListeners({AuditingEntityListener.class, AuditEmployeeListener.class}) //ahora sabrá que será auditado con fecha de creacion y actualizacion
@Entity
@Table(name="EMPLOYEES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString(exclude = {"position","department","orders"})
public class Employee extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name",nullable = false)
    private String lastName;
    @Column(nullable = false,unique = true)
    private String dni;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    private String  address;
    private String cellphone;
    private String bankAccountNumber;
    private String bankAccountCciNumber;
    @Column(nullable = false,columnDefinition = "numeric(8,2)")
    private Double salary;
    private Date birthday;
    private String photo;

    private boolean active=true;


    //Relaciones


    private Long departmentId;
     @ManyToOne(fetch = FetchType.EAGER) //EAGER carga inmediata, LAZY carga cuando se necesite
     @JoinColumn(name = "departmentId",referencedColumnName = "id", insertable = false, updatable = false)
    // @JsonIgnore //ignora la concatenacion infinita de
     private Department department;


    private Long positionId;
     @ManyToOne(fetch = FetchType.EAGER) //EAGER carga inmediata, LAZY carga cuando se necesite
     @JoinColumn(name = "positionId", referencedColumnName = "id", insertable = false, updatable = false)
    // @JsonIgnore
     private Position position;


     //Ordenes
     @OneToMany(mappedBy = "employee",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Order> orders;


     //Movimientos
     @OneToMany(mappedBy = "employee",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
     private List<Movement> movements;

}
