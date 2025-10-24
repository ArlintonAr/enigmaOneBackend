package com.enigmaOne.enigmaOne.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="POSITIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "employees")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String positionName;
    @Column(nullable = false,unique = true)
    private String code;

    @OneToMany(mappedBy = "position",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();

}
