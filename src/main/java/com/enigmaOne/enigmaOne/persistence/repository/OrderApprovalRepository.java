package com.enigmaOne.enigmaOne.persistence.repository;

import com.enigmaOne.enigmaOne.persistence.entity.OrderApproval;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrderApprovalRepository extends ListCrudRepository<OrderApproval,Long> {

    List<OrderApproval> findByOrderIdOrderByStepOrder(Long orderId);

    OrderApproval findFirstByOrderIdAndStatusOrderByStepOrder(Long orderId, ApprovalStatus status);

}

