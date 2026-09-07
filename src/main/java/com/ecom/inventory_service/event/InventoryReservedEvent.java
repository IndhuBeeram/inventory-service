package com.ecom.inventory_service.event;

import java.time.LocalDateTime;
import java.util.List;

public class InventoryReservedEvent {

    private Long orderId;
    private Long userId;
    private List<ReservedItem> items;
    private LocalDateTime reservedAt;

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