package com.ecom.inventory_service.consumer;

import com.ecom.inventory_service.event.InventoryFailedEvent;
import com.ecom.inventory_service.event.InventoryReservedEvent;
import com.ecom.inventory_service.event.OrderCreatedEvent;
import com.ecom.inventory_service.exception.InsufficientInventoryException;
import com.ecom.inventory_service.exception.InventoryNotFoundException;
import com.ecom.inventory_service.producer.InventoryEventProducer;
import com.ecom.inventory_service.service.InventoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventConsumer.class);

    private final InventoryService inventoryService;
    private final InventoryEventProducer inventoryEventProducer;

    public OrderEventConsumer(
            InventoryService inventoryService,
            InventoryEventProducer inventoryEventProducer) {

        this.inventoryService = inventoryService;
        this.inventoryEventProducer = inventoryEventProducer;
    }

    @KafkaListener(
            topics = "orders",
            groupId = "inventory-service-group"
    )
    public void consumeOrderCreatedEvent(
            OrderCreatedEvent event) {

        log.info(
                "Received OrderCreatedEvent for order ID: {}",
                event.getOrderId()
        );

        try {

            /*
             * Try to reserve the complete order.
             *
             * The Inventory Service checks all items
             * before updating anything.
             */
            inventoryService.reserveInventory(event);

            /*
             * Reservation successful.
             */
            InventoryReservedEvent reservedEvent =
                    buildReservedEvent(event);

            inventoryEventProducer
                    .publishInventoryReservedEvent(reservedEvent);

            log.info(
                    "Inventory reserved successfully for order ID: {}",
                    event.getOrderId()
            );

        } catch (InventoryNotFoundException
                 | InsufficientInventoryException exception) {

            /*
             * Inventory reservation failed because of a
             * business reason.
             */
            InventoryFailedEvent failedEvent =
                    buildFailedEvent(
                            event,
                            exception.getMessage()
                    );

            inventoryEventProducer
                    .publishInventoryFailedEvent(failedEvent);

            log.warn(
                    "Inventory reservation failed for order ID: {}. Reason: {}",
                    event.getOrderId(),
                    exception.getMessage()
            );
        }
    }

    private InventoryReservedEvent buildReservedEvent(
            OrderCreatedEvent event) {

        InventoryReservedEvent reservedEvent =
                new InventoryReservedEvent();

        reservedEvent.setOrderId(event.getOrderId());
        reservedEvent.setUserId(event.getUserId());
        reservedEvent.setReservedAt(LocalDateTime.now());

        List<InventoryReservedEvent.ReservedItem> items =
                event.getItems()
                        .stream()
                        .map(item -> {

                            InventoryReservedEvent.ReservedItem
                                    reservedItem =
                                    new InventoryReservedEvent.ReservedItem();

                            reservedItem.setProductId(
                                    item.getProductId()
                            );

                            reservedItem.setQuantity(
                                    item.getQuantity()
                            );

                            return reservedItem;
                        })
                        .toList();

        reservedEvent.setItems(items);

        return reservedEvent;
    }

    private InventoryFailedEvent buildFailedEvent(
            OrderCreatedEvent event,
            String reason) {

        InventoryFailedEvent failedEvent =
                new InventoryFailedEvent();

        failedEvent.setOrderId(event.getOrderId());
        failedEvent.setUserId(event.getUserId());
        failedEvent.setReason(reason);
        failedEvent.setFailedAt(LocalDateTime.now());

        List<InventoryFailedEvent.FailedItem> items =
                event.getItems()
                        .stream()
                        .map(item -> {

                            InventoryFailedEvent.FailedItem
                                    failedItem =
                                    new InventoryFailedEvent.FailedItem();

                            failedItem.setProductId(
                                    item.getProductId()
                            );

                            failedItem.setRequestedQuantity(
                                    item.getQuantity()
                            );

                            return failedItem;
                        })
                        .toList();

        failedEvent.setItems(items);

        return failedEvent;
    }
}