package com.microservice.deliveryalga.delivery.tracking.api.model;


import com.microservice.deliveryalga.delivery.tracking.domain.model.ContactPoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Deliveryinput {

    @NotNull
    @Valid
    private ContactPointInput sender;

    @NotNull
    @Valid
    private ContactPointInput recipient;

    @NotEmpty
    @Valid
    @Size(min = 1)
    private List<ItemInput> Items;


}
