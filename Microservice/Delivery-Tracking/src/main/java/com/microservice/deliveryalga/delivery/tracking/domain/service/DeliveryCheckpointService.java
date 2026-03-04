package com.microservice.deliveryalga.delivery.tracking.domain.service;

import com.microservice.deliveryalga.delivery.tracking.domain.exception.DomainException;
import com.microservice.deliveryalga.delivery.tracking.domain.model.Delivery;
import com.microservice.deliveryalga.delivery.tracking.domain.repository.DeliveryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryCheckpointService {

    private final DeliveryRepository deliveryRepository;

    public void place(UUID deliveryId){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DomainException());
        delivery.place();
        deliveryRepository.saveAndFlush(delivery);
    }
    public void pickUp(UUID deliveryId,UUID courierId){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DomainException());
        delivery.pickUp(courierId);
        deliveryRepository.saveAndFlush(delivery);
    }
    public void complete(UUID deliveryId){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DomainException());
        delivery.markAsDelivered();
        deliveryRepository.saveAndFlush(delivery);
    }


}
