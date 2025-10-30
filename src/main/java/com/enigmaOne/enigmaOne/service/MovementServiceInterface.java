package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Movement;
import com.enigmaOne.enigmaOne.persistence.types.ReturnableType;
import com.enigmaOne.enigmaOne.service.dto.MovementResponseDTO;

import java.util.List;

public interface MovementServiceInterface {


    List<MovementResponseDTO> getAllMovements();

    Movement getMovementById(Long id);

    boolean createMovement(Movement movement);

    boolean updateMovement(Long id, Movement movement);

    boolean deleteMovement(Long id);

    boolean existsMovementById(Long id);

    List<MovementResponseDTO> getMovementsByEmployeeId(Long employeeId);
    List<MovementResponseDTO> getMovementsByMaterialRequesterFirstName(String firstName);
    List<MovementResponseDTO> getMovementByTransactionCode(String transactionCode);
}
