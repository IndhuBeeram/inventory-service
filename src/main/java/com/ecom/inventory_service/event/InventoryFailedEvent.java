package com.ecom.inventory_service.event;

import java.time.LocalDateTime;
import java.util.List;

public class InventoryFailedEvent {

    private Long orderId;
    private Long userId;
    private String reason;
    private List<FailedItem> items;
    private LocalDateTime failedAt;

    public InventoryFailedEvent() {
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<FailedItem> getItems() {
        return items;
    }

    public void setItems(List<FailedItem> items) {
        this.items = items;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public static class FailedItem {

        private Long productId;
        private Integer requestedQuantity;

        public FailedItem() {
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getRequestedQuantity() {
            return requestedQuantity;
        }

        public void setRequestedQuantity(Integer requestedQuantity) {
            this.requestedQuantity = requestedQuantity;
        }
    }
}