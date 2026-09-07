package com.ecom.inventory_service.producer;

import com.ecom.inventory_service.event.InventoryFailedEvent;
import com.ecom.inventory_service.event.InventoryReservedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducer {

    private static final String INVENTORY_TOPIC = "inventory-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishInventoryReservedEvent(
            InventoryReservedEvent event) {

        kafkaTemplate.send(
                INVENTORY_TOPIC,
                String.valueOf(event.getOrderId()),
                event
        );
    }

    public void publishInventoryFailedEvent(
            InventoryFailedEvent event) {

        kafkaTemplate.send(
                INVENTORY_TOPIC,
                String.valueOf(event.getOrderId()),
                event
        );
    }
}