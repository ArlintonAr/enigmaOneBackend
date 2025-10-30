package com.enigmaOne.enigmaOne.persistence.repository;

import com.enigmaOne.enigmaOne.persistence.entity.Tracking;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface TrackingRepository extends ListCrudRepository<Tracking,Long> {
    boolean existsTrackingById(Long id);
    List<Tracking> findByTrackingState(TrackingState trackingState);

    // Devuelve el tracking más reciente para una orden (por id descendente)
    Tracking findTopByOrderIdOrderByIdDesc(Long orderId);

}
