package com.ecom.inventory_service.service;

import com.ecom.inventory_service.dto.InventoryRequest;
import com.ecom.inventory_service.dto.InventoryResponse;
import com.ecom.inventory_service.entity.Inventory;
import com.ecom.inventory_service.event.OrderCreatedEvent;
import com.ecom.inventory_service.exception.InsufficientInventoryException;
import com.ecom.inventory_service.exception.InventoryNotFoundException;
import com.ecom.inventory_service.repository.InventoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public InventoryResponse createInventory(InventoryRequest request) {

        if (inventoryRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Inventory already exists for product ID: "
                            + request.getProductId()
            );
        }

        Inventory inventory = new Inventory();

        inventory.setProductId(request.getProductId());
        inventory.setAvailableQuantity(request.getAvailableQuantity());
        inventory.setReservedQuantity(0);

        Inventory savedInventory = inventoryRepository.save(inventory);

        return mapToResponse(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId
                ));

        return mapToResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(
            Long productId,
            InventoryRequest request) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId
                ));

        inventory.setAvailableQuantity(
                request.getAvailableQuantity()
        );

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        return mapToResponse(updatedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long productId) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId
                ));

        inventoryRepository.delete(inventory);
    }

    @Override
    @Transactional
    public void reserveInventory(OrderCreatedEvent event) {

        /*
         * Step 1:
         * Combine duplicate product IDs if the same product
         * appears more than once in the order.
         */
        Map<Long, Integer> requestedQuantities =
                new LinkedHashMap<>();

        for (OrderCreatedEvent.OrderCreatedItem item : event.getItems()) {

            if (item.getProductId() == null
                    || item.getQuantity() == null
                    || item.getQuantity() <= 0) {

                throw new IllegalArgumentException(
                        "Invalid product or quantity in order"
                );
            }

            requestedQuantities.merge(
                    item.getProductId(),
                    item.getQuantity(),
                    Integer::sum
            );
        }

        /*
         * Step 2:
         * Load and lock every required inventory row.
         *
         * Nothing is updated yet.
         */
        Map<Long, Inventory> inventoryMap =
                new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry
                : requestedQuantities.entrySet()) {

            Long productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            Inventory inventory =
                    inventoryRepository
                            .findByProductIdForUpdate(productId)
                            .orElseThrow(() ->
                                    new InventoryNotFoundException(
                                            "Inventory not found for product ID: "
                                                    + productId
                                    ));

            /*
             * Step 3:
             * Check stock before making any update.
             */
            if (inventory.getAvailableQuantity()
                    < requestedQuantity) {

                throw new InsufficientInventoryException(
                        "Insufficient inventory for product ID: "
                                + productId
                                + ". Available: "
                                + inventory.getAvailableQuantity()
                                + ", Requested: "
                                + requestedQuantity
                );
            }

            inventoryMap.put(productId, inventory);
        }

        /*
         * Step 4:
         * All products have sufficient inventory.
         * Now update every inventory record.
         */
        for (Map.Entry<Long, Integer> entry
                : requestedQuantities.entrySet()) {

            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Inventory inventory = inventoryMap.get(productId);

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() - quantity
            );

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() + quantity
            );
        }

        /*
         * Step 5:
         * Save all changes.
         */
        inventoryRepository.saveAll(inventoryMap.values());
    }

    private InventoryResponse mapToResponse(
            Inventory inventory) {

        InventoryResponse response = new InventoryResponse();

        response.setId(inventory.getId());
        response.setProductId(inventory.getProductId());
        response.setAvailableQuantity(
                inventory.getAvailableQuantity()
        );
        response.setReservedQuantity(
                inventory.getReservedQuantity()
        );
        response.setCreatedAt(inventory.getCreatedAt());
        response.setUpdatedAt(inventory.getUpdatedAt());

        return response;
    }
}