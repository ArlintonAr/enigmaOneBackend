package com.enigmaOne.enigmaOne.persistence.repository;

import com.enigmaOne.enigmaOne.persistence.entity.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//CRUD REPOSITORY tambien se puede usar
public interface EmployeeRepository extends ListCrudRepository<Employee, Long> {

    Employee findEmployeeByEmail(String email);
    List<Employee> findEmployeeByFirstNameContainingIgnoreCase(String name);
    boolean existsById(Long id);
    boolean existsEmployeeByDni(String dni);
    boolean existsEmployeeByEmail(String email);


    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "e.dni LIKE CONCAT('%', :term, '%') OR " +
            "CAST(e.id AS string) LIKE CONCAT('%', :term, '%')")
    List<Employee> findByAnyTerm(@Param("term") String term);

}
