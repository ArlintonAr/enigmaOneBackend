package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.ServiceOrder;
import com.enigmaOne.enigmaOne.persistence.entity.Tracking;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;

import java.util.List;

public interface TrackingServiceInterface {

    List<Tracking> getAllTracking();
    Tracking findById(Long id);
    boolean saveTracking(Tracking tracking);
    boolean updateTracking(Tracking tracking,Long id);
    boolean deleteTracking(Long id);
    List<Tracking> findByTrackingState(TrackingState trackingState);

}
