package com.enigmaOne.enigmaOne.service;


import com.enigmaOne.enigmaOne.persistence.entity.Tracking;
import com.enigmaOne.enigmaOne.persistence.repository.TrackingRepository;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;
import com.enigmaOne.enigmaOne.service.mapper.TrackingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrackingService implements TrackingServiceInterface {

    @Autowired
    private  TrackingRepository trackingRepository;

    @Autowired
    private TrackingMapper trackingMapper;


    @Override
    public List<Tracking> getAllTracking() {
        return this.trackingRepository.findAll();
    }

    @Override
    public List<Tracking> findByTrackingState(TrackingState trackingState) {
        return this.trackingRepository.findByTrackingState(trackingState);
    }

    @Override
    public Tracking findById(Long id) {
        return this.trackingRepository.findById(id).orElse(null);
    }

    @Override
    public boolean saveTracking(Tracking tracking) {
        try {
                this.trackingRepository.save(tracking);
                return true;
        }catch (Exception e){
            System.out.println("ERROR: " +e);
            return  false;
        }
    }

    @Override
    @Transactional
    public boolean updateTracking(Tracking tracking, Long id) {
        try {

            Tracking findTracking = this.findById(id);
            tracking.setId(findTracking.getId());


            this.trackingMapper.updateTrackingFromDto(tracking,findTracking);
            this.trackingRepository.save(findTracking);
            return true;

        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public boolean deleteTracking(Long id) {
        try {
            Tracking tracking = this.findById(id);
            this.trackingRepository.delete(tracking);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }

    }



    public boolean existTrackingById(Long id) {
        return this.trackingRepository.existsById(id);
    }
}
