package com.microservice.deliveryalga.delivery.tracking.domain.model;
import com.microservice.deliveryalga.delivery.tracking.domain.exception.DomainException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter(AccessLevel.PRIVATE)
@Getter
public class Delivery {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID courierId;

    private DeliveryStatus status;

    private OffsetDateTime placeAt;
    private OffsetDateTime assignedAt;
    private OffsetDateTime expectedDeliveryAt;
    private OffsetDateTime fulfilledAt;

    private BigDecimal distanceFee;
    private BigDecimal courierPayout;
    private BigDecimal totalCost;

    private Integer totalItems;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zipCode",column = @Column(name = "sender_zip_code")),
            @AttributeOverride(name = "street",column = @Column(name = "sender_street")),
            @AttributeOverride(name = "number",column = @Column(name = "sender_number")),
            @AttributeOverride(name = "complement",column = @Column(name = "sender_complement")),
            @AttributeOverride(name = "name",column = @Column(name = "sender_name")),
            @AttributeOverride(name = "phone",column = @Column(name = "sender_phone"))
    })
    private ContactPoint sender;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zipCode",column = @Column(name = "recipient_zip_code")),
            @AttributeOverride(name = "street",column = @Column(name = "recipient_street")),
            @AttributeOverride(name = "number",column = @Column(name = "recipient_number")),
            @AttributeOverride(name = "complement",column = @Column(name = "recipient_complement")),
            @AttributeOverride(name = "name",column = @Column(name = "recipient_name")),
            @AttributeOverride(name = "phone",column = @Column(name = "recipient_phone"))
    })
    private ContactPoint recipient;

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "delivery")
    private List<Item> items = new ArrayList<>();

    public static Delivery draft(){
        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setStatus(DeliveryStatus.DRAFT);
        delivery.setTotalItems(0);
        delivery.setTotalCost(BigDecimal.ZERO);
        delivery.setCourierPayout(BigDecimal.ZERO);
        delivery.setDistanceFee(BigDecimal.ZERO);
        return delivery;
    }

    public UUID addItem(String name, int quantity){
        Item item = Item.brandNew(name, quantity,this);
        items.add(item);
        calculateTotalItems();
        return item.getId();
    }

    public void changeItemQuantity(UUID ItemId, int newQuantity){
        Item item = getItems().stream().filter(i -> i.getId().equals(ItemId))
                .findFirst().orElseThrow();

        item.setQuantity(newQuantity);
        calculateTotalItems();
    }

    public void removeItems(UUID itemId){
        items.removeIf(item -> item.getId().equals(itemId));
        calculateTotalItems();
    }

    public void removeItem(){
        items.clear();
        calculateTotalItems();
    }

    public void editPreparationDetails(PreparationDetails details){
        verifyIfCanBeEdited();
        setSender(details.getSender());
        setRecipient(details.getRecipient());
        setDistanceFee(details.getDistanceFee());
        setCourierPayout(details.getCourierPayout());

        setExpectedDeliveryAt(OffsetDateTime.now().plus(details.getExpectedDeliveryTime()));
        setTotalCost(this.getDistanceFee().add(this.getCourierPayout()));
    }

    public void place(){
        verifyIfCanBePlaced();
        this.changeStatus(DeliveryStatus.WAITING_FOR_COURIER);
        this.setPlaceAt(OffsetDateTime.now());
    }

    public void pickUp(UUID courierId){
        this.setCourierId(courierId);
        this.changeStatus(DeliveryStatus.IN_TRANSIT);
        this.setAssignedAt(OffsetDateTime.now());
    }

    public void markAsDelivered(){
        this.changeStatus(DeliveryStatus.DELIVERED);
        this.setFulfilledAt(OffsetDateTime.now());
    }

    public List<Item> getItems(){
        return Collections.unmodifiableList(this.items);
    }

    private void calculateTotalItems(){
        int totalItems = getItems().stream().mapToInt(Item::getQuantity).sum();
        setTotalItems(totalItems);
    }

    private void verifyIfCanBePlaced() {
        if(!isFilled()){
            throw new DomainException("Falta de informações de envio");
        }
        if(!getStatus().equals(DeliveryStatus.DRAFT)){
            throw new DomainException("A entrega em questão não é um rascunho");
        }

    }


    private void verifyIfCanBeEdited() {
         if(!getStatus().equals(DeliveryStatus.DRAFT)){
             throw new DomainException("Para editar a entrega em questão " +
                     "é necessario que ela seja um rascunho");
         }
    }

    private boolean isFilled() {
         return this.getSender() != null
                 && this.getRecipient() != null
                 && this.getTotalCost() != null;
    }

    private void changeStatus(DeliveryStatus newStatus){
        if(newStatus != null && this.getStatus().canNotChangeTo(newStatus) ){
            throw new DomainException(" o status " + this.getStatus() + " nao pode ser" +
                    " transferido para " + newStatus);
        }
        this.setStatus(newStatus);
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PreparationDetails{
        private ContactPoint sender;
        private ContactPoint recipient;
        private BigDecimal distanceFee;
        private BigDecimal courierPayout;
        private Duration expectedDeliveryTime;
    }


}
