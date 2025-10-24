package com.enigmaOne.enigmaOne.persistence.repository;

import com.enigmaOne.enigmaOne.persistence.entity.Movement;
import com.enigmaOne.enigmaOne.persistence.types.ReturnableType;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface MovementRepository extends ListCrudRepository<Movement, Long> {

    boolean existsById(Long id);

   boolean existsMovementByTransactionCode(String transactionCode);
   List<Movement> findMovementByEmployeeId(Long employeeId);
   List<Movement> findMovementByMaterialRequerterFirstName(String firstName);


}
