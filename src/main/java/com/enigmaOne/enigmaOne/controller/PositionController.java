package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.persistence.entity.Position;
import com.enigmaOne.enigmaOne.service.PositionService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/positions")
public class PositionController {

    private PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<Position>>> getAllPositions() {

        List<Position> positions = positionService.getAllPositions();
        if (positions.isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse<>( "Sin roles", null));
        } else {
            return ResponseEntity.ok(new ApiResponse<>("Lista de roles", positions));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Position>> getPositionById(@PathVariable Long id) {
        Position position = positionService.getPositionById(id);
        if (position == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("Rol no encontrado", null));
        } else {
            return ResponseEntity.ok(new ApiResponse<>("Rol encontrado", position));
        }
    }

    @PostMapping("/createPosition")
    public ResponseEntity<ApiResponse<Position>> createPosition(@RequestBody Position position) {

        boolean created = positionService.createPosition(position);

        if (created) {
            return ResponseEntity.status(201).body(new ApiResponse<>("Rol creado", position));
        } else {
            return ResponseEntity.status(400).body(new ApiResponse<>("Error al crear el rol", null));
        }
    }

    @PatchMapping("/updatePosition/{id}")
    public ResponseEntity<ApiResponse<Position>> updatePosition(@PathVariable Long id, @RequestBody Position position) {

        if (!this.positionService.existsPositionById(id)) {
            return ResponseEntity.status(404).body(new ApiResponse<>("Rol no encontrado", null));
        }

        boolean updated = positionService.updatePosition(id, position);
        if (updated) {
            Position updatedPosition = positionService.getPositionById(id);
            return ResponseEntity.ok(new ApiResponse<>("Rol actualizado", updatedPosition));
        } else {
            return ResponseEntity.status(400).body(new ApiResponse<>("Error al actualizar el rol", null));
        }
    }


    @DeleteMapping("/deletePosition/{id}")
    public ResponseEntity<ApiResponse<Position>> deletePosition(@PathVariable Long id) {
        if (!this.positionService.existsPositionById(id)) {
            return ResponseEntity.status(404).body(new ApiResponse<>("Rol no encontrado", null));
        }
        boolean deleted = positionService.deletePosition(id);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponse<>("Rol eliminado", null));
        } else {
            return ResponseEntity.status(400).body(new ApiResponse<>("Error al eliminar el rol", null));
        }
    }

}
