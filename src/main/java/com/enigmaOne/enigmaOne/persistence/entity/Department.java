package com.enigmaOne.enigmaOne.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString()
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String name;
    @Column(nullable = false,unique = true)
    private String code;

    @Column(nullable = true)
    private boolean state = true;

    private Date created_at;
    private Date updated_at;


    @OneToMany(mappedBy = "department",fetch =FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();

}
