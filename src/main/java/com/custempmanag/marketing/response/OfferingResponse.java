package com.custempmanag.marketing.response;

import com.custempmanag.marketing.model.AvailableStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class OfferingResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private String categoryName;
    private Integer stock;
    private String image;
    private AvailableStatus availableStatus;
    private Long ownerId;
    private Instant createdAt;
    private Instant updatedAt;
}
