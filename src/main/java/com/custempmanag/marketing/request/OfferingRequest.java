package com.custempmanag.marketing.request;

import com.custempmanag.marketing.model.AvailableStatus;
import com.custempmanag.marketing.model.CurrencyTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OfferingRequest {
    @NotBlank(message = "{name.notblank}")
    @Size(min = 2, max = 100, message = "{name.size}")
    private String name;

    @Size(max = 500, message = "{description.size}")
    private String description;

    private String image;

    @Positive(message = "{price.positive}")
    private BigDecimal price;

    private String categoryName;

    private CurrencyTypes currency;

    private AvailableStatus availableStatus;

    private int stock;

    private String location;

}
