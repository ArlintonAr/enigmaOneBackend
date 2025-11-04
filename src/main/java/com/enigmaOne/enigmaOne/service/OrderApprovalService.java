package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.entity.OrderApproval;
import com.enigmaOne.enigmaOne.persistence.entity.Employee;
import com.enigmaOne.enigmaOne.persistence.repository.OrderApprovalRepository;
import com.enigmaOne.enigmaOne.persistence.repository.OrderRepository;
import com.enigmaOne.enigmaOne.persistence.repository.EmployeeRepository;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalRole;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;
import com.enigmaOne.enigmaOne.service.dto.ApprovalActionDTO;
import com.enigmaOne.enigmaOne.service.dto.ApprovalResultDTO;
import com.enigmaOne.enigmaOne.Config.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderApprovalService {

    @Autowired
    private OrderApprovalRepository orderApprovalRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TrackingService trackingService;

    // Inicializa las 3 aprobaciones cuando se crea una orden
    public void initializeApprovals(Order order){
        try{
            // Si ya existen aprobaciones para esta orden, no crear duplicados
            List<OrderApproval> existing = this.orderApprovalRepository.findByOrderIdOrderByStepOrder(order.getId());
            if(existing != null && !existing.isEmpty()){
                return;
            }
            List<OrderApproval> approvals = new ArrayList<>();
            OrderApproval a1 = new OrderApproval();
            a1.setOrderId(order.getId());
            a1.setRole(ApprovalRole.ADMINISTRADOR);
            a1.setStatus(ApprovalStatus.PENDIENTE);
            a1.setStepOrder(1);

            OrderApproval a2 = new OrderApproval();
            a2.setOrderId(order.getId());
            a2.setRole(ApprovalRole.JEFE_DE_PROYECTO);
            a2.setStatus(ApprovalStatus.PENDIENTE);
            a2.setStepOrder(2);

            OrderApproval a3 = new OrderApproval();
            a3.setOrderId(order.getId());
            a3.setRole(ApprovalRole.GERENTE_GENERAL);
            a3.setStatus(ApprovalStatus.PENDIENTE);
            a3.setStepOrder(3);

            approvals.add(a1);
            approvals.add(a2);
            approvals.add(a3);

            this.orderApprovalRepository.saveAll(approvals);

        }catch (Exception e){
            System.out.println("ERROR init approvals: "+e);
            throw e;
        }
    }

    public List<OrderApproval> listApprovals(Long orderId){
        return this.orderApprovalRepository.findByOrderIdOrderByStepOrder(orderId);
    }

    @Transactional
    public ApprovalResultDTO approve(Long orderId, ApprovalRole role, ApprovalActionDTO action){
        OrderApproval next = this.orderApprovalRepository.findFirstByOrderIdAndStatusOrderByStepOrder(orderId, ApprovalStatus.PENDIENTE);
        if(next == null){
            throw new RuntimeException("No hay aprobaciones pendientes para la orden " + orderId);
        }
        if(next.getRole() != role){
            throw new RuntimeException("Se esperaba que apruebe : " + next.getRole() + " pero fue: " + role);
        }

        // resolver approverId desde la autenticación (forzar uso del usuario autenticado)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)){
            throw new RuntimeException("Usuario no autenticado. Debe iniciar sesión para aprobar.");
        }
        Long resolvedApproverId = ((CustomUserDetails) authentication.getPrincipal()).getId();

        next.setApproverId(resolvedApproverId);
        Employee emp = this.employeeRepository.findById(resolvedApproverId).orElse(null);
        if(emp != null){
            next.setApproverName(emp.getFirstName() + " " + emp.getLastName());
        }
        next.setComments(action.getComments());
        next.setApprovedAt(new Date());
        next.setStatus(ApprovalStatus.APROBADO);

        this.orderApprovalRepository.save(next);

        // Tracking centralizado
        // Only set tracking to APPROVED when the whole order is approved (final step).

        // check if there are remaining pending approvals
        OrderApproval remaining = this.orderApprovalRepository.findFirstByOrderIdAndStatusOrderByStepOrder(orderId, ApprovalStatus.PENDIENTE);
        if(remaining == null){
            // all approved -> mark order as APPROVED
            Order order = this.orderRepository.findById(orderId).orElse(null);
            if(order != null){
                order.setApprovalStatus(com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus.APROBADO);
                this.orderRepository.save(order);

                // Update or create single tracking record to APROBADO
                String actorName = emp != null ? emp.getFirstName() + " " + emp.getLastName() : null;
                this.trackingService.updateOrCreateTrackingEvent(orderId, TrackingState.APROBADO, resolvedApproverId, actorName, "Orden aprobada por todos los pasos");
            }
        }

        ApprovalResultDTO result = new ApprovalResultDTO();
        result.setOrder(this.orderRepository.findById(orderId).orElse(null));
        result.setApprovals(this.orderApprovalRepository.findByOrderIdOrderByStepOrder(orderId));
        return result;
    }

    @Transactional
    public ApprovalResultDTO reject(Long orderId, ApprovalRole role, ApprovalActionDTO action){
        OrderApproval next = this.orderApprovalRepository.findFirstByOrderIdAndStatusOrderByStepOrder(orderId, ApprovalStatus.PENDIENTE);
        if(next == null){
            throw new RuntimeException("No hay aprobaciones pendientes para la orden " + orderId);
        }
        if(next.getRole() != role){
            throw new RuntimeException("Desajuste en el rol de aprobación. Esperado: " + next.getRole() + " pero fue: " + role);
        }

        // resolver approverId desde la autenticación (forzar uso del usuario autenticado)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)){
            throw new RuntimeException("Usuario no autenticado. Debe iniciar sesión para rechazar.");
        }
        Long resolvedApproverId = ((CustomUserDetails) authentication.getPrincipal()).getId();

        next.setApproverId(resolvedApproverId);
        Employee emp = this.employeeRepository.findById(resolvedApproverId).orElse(null);
        if(emp != null){
            next.setApproverName(emp.getFirstName() + " " + emp.getLastName());
        }
        next.setComments(action.getComments());
        next.setApprovedAt(new Date());
        next.setStatus(ApprovalStatus.RECHAZADO);

        this.orderApprovalRepository.save(next);

        // Tracking centralizado
        String actorName = emp != null ? emp.getFirstName() + " " + emp.getLastName() : null;
        // Update the single tracking record to RECHAZADO
        this.trackingService.updateOrCreateTrackingEvent(orderId, TrackingState.RECHAZADO, resolvedApproverId, actorName, action.getComments());

        // mark order as REJECTED
        Order order = this.orderRepository.findById(orderId).orElse(null);
        if(order != null){
            order.setApprovalStatus(com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus.RECHAZADO);
            this.orderRepository.save(order);
        }

        ApprovalResultDTO result = new ApprovalResultDTO();
        result.setOrder(this.orderRepository.findById(orderId).orElse(null));
        result.setApprovals(this.orderApprovalRepository.findByOrderIdOrderByStepOrder(orderId));
        return result;
    }

}
