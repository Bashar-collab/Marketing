package com.custempmanag.marketing.response;

import com.custempmanag.marketing.model.AvailableStatus;
import com.custempmanag.marketing.model.Image;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OfferingResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private String categoryName;
    private Integer stock;
    private List<ImageResponse> image; // Holds imageId and image URL
    private AvailableStatus availableStatus;
    private Long ownerId;
    private Instant createdAt;
    private Instant updatedAt;
    private Number ratingValue;

    public OfferingResponse() {
    }

    public OfferingResponse(Long id, String name, BigDecimal price, List<ImageResponse> image, Number ratingValue) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.ratingValue = ratingValue;
    }

    public OfferingResponse(Long id, String name, BigDecimal price, Number ratingValue) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.ratingValue = ratingValue == null ? 0.0 : ratingValue.doubleValue();
    }

    public OfferingResponse(Long id, String name, Long ownerId, BigDecimal price, Number ratingValue) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.price = price;
        this.ratingValue = ratingValue == null ? 0.0 : ratingValue.doubleValue();
    }

    public OfferingResponse(Long id, String name, String description, BigDecimal price, String currency,
                            String categoryName, Integer stock, List<ImageResponse> image,
                            AvailableStatus availableStatus, Long ownerId, Instant createdAt,
                            Instant updatedAt, Double ratingValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.categoryName = categoryName;
        this.stock = stock;
        this.image = image;
        this.availableStatus = availableStatus;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ratingValue = ratingValue;
    }
}
