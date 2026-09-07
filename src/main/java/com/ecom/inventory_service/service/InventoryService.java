package com.ecom.inventory_service.service;

import com.ecom.inventory_service.dto.InventoryRequest;
import com.ecom.inventory_service.dto.InventoryResponse;
import com.ecom.inventory_service.event.OrderCreatedEvent;

import java.util.List;

public interface InventoryService {

    InventoryResponse createInventory(InventoryRequest request);

    InventoryResponse getInventoryByProductId(Long productId);

    List<InventoryResponse> getAllInventory();

    InventoryResponse updateInventory(Long productId, InventoryRequest request);

    void deleteInventory(Long productId);

    void reserveInventory(OrderCreatedEvent event);
}