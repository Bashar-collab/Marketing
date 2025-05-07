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
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private String image;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private String categoryName;

    private CurrencyTypes currency;

    private AvailableStatus availableStatus;

    private int stock;

    private String location;

}
