package com.microservice.deliveryalga.delivery.tracking.domain.service;

import com.microservice.deliveryalga.delivery.tracking.api.model.ContactPointInput;
import com.microservice.deliveryalga.delivery.tracking.api.model.Deliveryinput;
import com.microservice.deliveryalga.delivery.tracking.api.model.ItemInput;
import com.microservice.deliveryalga.delivery.tracking.domain.exception.DomainException;
import com.microservice.deliveryalga.delivery.tracking.domain.model.ContactPoint;
import com.microservice.deliveryalga.delivery.tracking.domain.model.Delivery;
import com.microservice.deliveryalga.delivery.tracking.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryPreparationService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public Delivery draft(Deliveryinput input) {
        Delivery delivery = Delivery.draft();
        handlePreparation(input, delivery);
        return deliveryRepository.saveAndFlush(delivery);
    }

    @Transactional
    public Delivery edit(UUID deliveryId,Deliveryinput input) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DomainException());
        delivery.removeItem();
        handlePreparation(input, delivery);
        return deliveryRepository.saveAndFlush(delivery);
    }

    private void handlePreparation(Deliveryinput input, Delivery delivery) {
        ContactPointInput senderInput = input.getSender();
        ContactPointInput recipientInput = input.getRecipient();

        ContactPoint sender = ContactPoint.builder()
                .phone(senderInput.getPhone())
                .name(senderInput.getName())
                .complement(senderInput.getComplement())
                .number(senderInput.getNumber())
                .zipCode(senderInput.getZipcode())
                .street(senderInput.getStreet())
                .build();
        ContactPoint recipient = ContactPoint.builder()
                .phone(recipientInput.getPhone())
                .name(recipientInput.getName())
                .complement(recipientInput.getComplement())
                .number(recipientInput.getNumber())
                .zipCode(recipientInput.getZipcode())
                .street(recipientInput.getStreet())
                .build();

        Duration expectedDeliveryTime = Duration.ofHours(3);

        BigDecimal distanceFee = new  BigDecimal("10");
        BigDecimal payout = new BigDecimal("10");

        Delivery.PreparationDetails preparationDetails = Delivery.PreparationDetails.builder()
                .recipient(recipient)
                .sender(sender)
                .expectedDeliveryTime(expectedDeliveryTime)
                .courierPayout(payout)
                .distanceFee(distanceFee)
                .build();

        delivery.editPreparationDetails(preparationDetails);

        for(ItemInput itemInput : input.getItems()) {
            delivery.addItem(itemInput.getName(), itemInput.getQuantity());
        }

    }


}
