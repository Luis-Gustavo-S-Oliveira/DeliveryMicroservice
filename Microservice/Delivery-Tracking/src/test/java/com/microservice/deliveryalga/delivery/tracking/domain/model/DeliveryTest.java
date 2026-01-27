package com.microservice.deliveryalga.delivery.tracking.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {
    @Test
    public void shouldChangeStatusToPlaced(){
        Delivery delivery = Delivery.draft();

        delivery.editPreparationDetails(detalhesDeCriacaoValidosPreparecao());

        delivery.place();

        assertEquals(DeliveryStatus.WAITING_FOR_COURIER, delivery.getStatus());
        assertNotNull(delivery.getPlaceAt());
    }

    private Delivery.PreparationDetails detalhesDeCriacaoValidosPreparecao(){
        ContactPoint sender = ContactPoint.builder()
                .zipCode("00000-000")
                .street("Mario tagalera da silva")
                .number("200")
                .complement("Adega")
                .name("luis gustavo")
                .phone("(19)99967-1356")
                .build();

        ContactPoint recipient = ContactPoint.builder()
                .zipCode("05600-000")
                .street("Mario calado da silva")
                .number("21")
                .complement("predio A, bloco 1")
                .name("gustavo luis")
                .phone("(19)99879-1536")
                .build();


        return Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("15.00"))
                .courierPayout(new BigDecimal("100"))
                .expectedDeliveryTime(Duration.ofHours(1))
                .build();
    }

}