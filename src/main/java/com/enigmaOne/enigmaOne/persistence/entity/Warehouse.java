package com.enigmaOne.enigmaOne.persistence.entity;

import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@EntityListeners( {AuditingEntityListener.class})
@Entity
@Table(name = "WAREHOUSES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String locationName;
    private String latitude;
    private String longitude;


    //Relaciones

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Stock> stocks;

}
