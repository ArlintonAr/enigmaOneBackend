package com.enigmaOne.enigmaOne.persistence.entity;

import com.enigmaOne.enigmaOne.persistence.audit.Auditable;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalRole;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@EntityListeners({AuditingEntityListener.class})
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity(name = "ORDER_APPROVALS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderApproval extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orderId", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDIENTE;

    private Long approverId;
    private String approverName;
    private Date approvedAt;

    @Column(columnDefinition = "text")
    private String comments;

    private Integer stepOrder;

}
