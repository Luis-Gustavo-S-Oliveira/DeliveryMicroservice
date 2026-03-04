package com.microservice.deliveryalga.delivery.tracking.domain.repository;

import com.microservice.deliveryalga.delivery.tracking.domain.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {







}
