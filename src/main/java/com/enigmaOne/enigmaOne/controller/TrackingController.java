package com.enigmaOne.enigmaOne.controller;


import com.enigmaOne.enigmaOne.persistence.entity.Tracking;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;
import com.enigmaOne.enigmaOne.service.TrackingService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trackings")
public class TrackingController {

    private final TrackingService trackingService;

    @Autowired
    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Tracking>>> getAllTrackings() {
        List<Tracking> trackings = trackingService.getAllTracking();
        if (trackings.isEmpty()) {
            ApiResponse<List<Tracking>> response = new ApiResponse<>("No hay seguimientos disponibles", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<List<Tracking>> response = new ApiResponse<>("Lista de seguimientos", trackings);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/byState/{state}")
    public  ResponseEntity<ApiResponse<List<Tracking>>> getTrackingsByState(@PathVariable TrackingState state) {
        List<Tracking> trackings = trackingService.findByTrackingState(state);
        if (trackings.isEmpty()) {
            ApiResponse<List<Tracking>> response = new ApiResponse<>("No hay seguimientos con el estado especificado", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<List<Tracking>> response = new ApiResponse<>("Lista de seguimientos con el estado especificado", trackings);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tracking>> getTrackingById(@PathVariable Long id) {
        Tracking tracking = trackingService.findById(id);
        if (tracking == null) {
            ApiResponse<Tracking> response = new ApiResponse<>("Seguimiento no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<Tracking> response = new ApiResponse<>("Seguimiento encontrado", tracking);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/createTracking")
    public ResponseEntity<ApiResponse<Tracking>> createTracking(@RequestBody Tracking tracking) {

        boolean savedTracking = trackingService.saveTracking(tracking);
        ApiResponse<Tracking> response;
        if (!savedTracking) {
            response = new ApiResponse<>("Seguimiento NO creado", tracking);
            return ResponseEntity.status(500).body(response);
        } else {
            response = new ApiResponse<>("Seguimiento creado", tracking);
            return ResponseEntity.status(201).body(response);
        }
    }

    @PatchMapping("/updateTracking/{id}")
    public ResponseEntity<ApiResponse<Tracking>> updateTracking(@RequestBody Tracking tracking, @PathVariable Long id) {

        if (!this.trackingService.existTrackingById(id)){
            ApiResponse<Tracking> response = new ApiResponse<>("El seguimiento no existe", null);
            return ResponseEntity.status(404).body(response);
        }

        boolean updatedTracking = trackingService.updateTracking(tracking, id);
        ApiResponse<Tracking> response;
        if (!updatedTracking) {
            response = new ApiResponse<>("Seguimiento NO actualizado", tracking);
            return ResponseEntity.status(500).body(response);
        }
        Tracking updated = trackingService.findById(id);
        response = new ApiResponse<>("Seguimiento actualizado", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteTracking/{id}")
    public ResponseEntity<ApiResponse<Tracking>> deleteTracking(@PathVariable Long id) {

        if (!this.trackingService.existTrackingById(id)){
            ApiResponse<Tracking> response = new ApiResponse<>("El seguimiento no existe", null);
            return ResponseEntity.status(404).body(response);
        }
        boolean deletedTracking = trackingService.deleteTracking(id);

        ApiResponse<Tracking> response;
        if (!deletedTracking) {
            response = new ApiResponse<>("Seguimiento NO eliminado", null);
            return ResponseEntity.status(500).body(response);
        }
        response = new ApiResponse<>("Seguimiento eliminado", null);
        return ResponseEntity.ok(response);
    }

}
