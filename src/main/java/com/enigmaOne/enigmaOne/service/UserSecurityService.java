package com.enigmaOne.enigmaOne.service;


import com.enigmaOne.enigmaOne.Config.CustomUserDetails;
import com.enigmaOne.enigmaOne.persistence.entity.Employee;
import com.enigmaOne.enigmaOne.persistence.entity.Position;
import com.enigmaOne.enigmaOne.persistence.repository.EmployeeRepository;
import com.enigmaOne.enigmaOne.persistence.repository.PositionRepository;
import com.enigmaOne.enigmaOne.persistence.types.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserSecurityService implements UserDetailsService {



    private final EmployeeRepository employeeRepository;


    @Autowired
    public UserSecurityService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

      Employee employee =  this.employeeRepository.findEmployeeByEmail(email);

        if (employee == null) {
            throw new UsernameNotFoundException("Empleado no encontrado con ese email: " + email);
        }

       //Armamos la lista de authirities usando SimpleGrantedAuthority
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + employee.getPosition().getPositionName().toUpperCase()));

        return new CustomUserDetails(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPassword(),
                employee.getPhoto(),
                authorities
        );

        /*
        return User.builder()
                .username(employee.getEmail())
                .password(employee.getPassword())
                .roles(employee.getPosition().getPositionName().toUpperCase())
                //.authorities()
               // .accountLocked(employee.isActive())
                .build(); */
    }




}
