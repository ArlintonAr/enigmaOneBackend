package com.enigmaOne.enigmaOne.service;


import com.enigmaOne.enigmaOne.persistence.entity.Tracking;
import com.enigmaOne.enigmaOne.persistence.repository.TrackingRepository;
import com.enigmaOne.enigmaOne.persistence.repository.OrderRepository;
import com.enigmaOne.enigmaOne.persistence.entity.Order;
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

    @Autowired
    private OrderRepository orderRepository;


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

    /**
     * Helper para crear y guardar un Tracking de forma consistente.
     * Evita duplicar la construcción del objeto Tracking en múltiples servicios.
     * Además actualiza el campo Order.currentTrackingState con el nuevo estado.
     * Este método crea siempre un nuevo registro histórico.
     */
    @Transactional
    public boolean saveTrackingEvent(Long orderId, TrackingState state, Long actorId, String actorName, String note){
        try{
            Tracking t = new Tracking();
            t.setOrderId(orderId);
            t.setTrackingState(state != null ? state : TrackingState.PEDIDO);
            t.setActorId(actorId);
            t.setActorName(actorName);
            t.setNote(note);
            this.trackingRepository.save(t);

            // Update order currentTrackingState for quick access
            if(orderId != null && state != null){
                Order order = this.orderRepository.findById(orderId).orElse(null);
                if(order != null){
                    order.setCurrentTrackingState(state);
                    this.orderRepository.save(order);
                }
            }

            return true;
        }catch (Exception e){
            System.out.println("ERROR saveTrackingEvent: " + e);
            return false;
        }
    }

    /**
     * Actualiza el tracking más reciente de la orden si existe; si no existe,
     * crea uno nuevo. Esto evita insertar múltiples registros históricos para
     * estados que deben representarse como "estado actual" por orden.
     */
    @Transactional
    public boolean updateOrCreateTrackingEvent(Long orderId, TrackingState state, Long actorId, String actorName, String note){
        try{
            if(orderId == null || state == null) return false;
            Tracking existing = this.trackingRepository.findTopByOrderIdOrderByIdDesc(orderId);
            if(existing != null){
                // update existing entry to new state
                existing.setTrackingState(state);
                existing.setActorId(actorId);
                existing.setActorName(actorName);
                existing.setNote(note);
                this.trackingRepository.save(existing);
            } else {
                // no existing, create new
                Tracking t = new Tracking();
                t.setOrderId(orderId);
                t.setTrackingState(state);
                t.setActorId(actorId);
                t.setActorName(actorName);
                t.setNote(note);
                this.trackingRepository.save(t);
            }

            // Sync order.currentTrackingState
            Order order = this.orderRepository.findById(orderId).orElse(null);
            if(order != null){
                order.setCurrentTrackingState(state);
                this.orderRepository.save(order);
            }

            return true;
        }catch (Exception e){
            System.out.println("ERROR updateOrCreateTrackingEvent: " + e);
            return false;
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
