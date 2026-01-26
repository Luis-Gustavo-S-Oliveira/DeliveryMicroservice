package com.microservice.deliveryalga.delivery.tracking.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
public class ContactPoint {
    private String zipCode;
    private String Street;
    private String number;
    private String complement;
    private String name;
    private String phone;
}
