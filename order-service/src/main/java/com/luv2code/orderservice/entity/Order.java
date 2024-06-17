package com.luv2code.orderservice.entity;

import com.luv2code.commonevents.event.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "t_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String orderNumber;

    @Enumerated(EnumType.STRING)

    private OrderStatus orderStatus;

    private String customerName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderLineItem> orderLineItems = new java.util.ArrayList<>();

    private LocalDateTime orderDate;

    public void setOrderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
        orderLineItems.forEach(orderLineItem -> orderLineItem.setOrder(this));
    }

    @PrePersist
    private void preInsert() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }

        if (this.orderStatus == null) {
            orderStatus = OrderStatus.ORDER_ACCEPTED;
        }
    }

}