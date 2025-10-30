package com.enigmaOne.enigmaOne.service.dto;

import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.entity.OrderApproval;
import lombok.Data;

import java.util.List;

@Data
public class ApprovalResultDTO {
    private Order order;
    private List<OrderApproval> approvals;
}

