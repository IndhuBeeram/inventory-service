package com.ecom.inventory_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryReservedEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private List<ReservedItem> items;
    private LocalDateTime reservedAt;
    private PaymentType paymentType;
    

    public InventoryReservedEvent() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<ReservedItem> getItems() {
        return items;
    }

    public void setItems(List<ReservedItem> items) {
        this.items = items;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public static class ReservedItem {

        private Long productId;
        private Integer quantity;

        public ReservedItem() {
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}