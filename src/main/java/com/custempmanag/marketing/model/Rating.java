package com.custempmanag.marketing.model;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.Data;
import org.hibernate.annotations.*;

import java.security.Timestamp;
import java.time.Instant;

@Entity
@Data
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

//    private String rateableType;

    @Column(name = "rateable_id")
    private long rateableId;

    @Column(name = "rateable_type")
    private String rateableType;

    private double ratingValue;

//    @Any
//    @AnyDiscriminator(DiscriminatorType.STRING)
//    @AnyDiscriminatorValue(discriminator = "Offering", entity = Offering.class)
//    @AnyKeyJavaClass(Long.class)
//    @Column(name = "rateable_type", insertable = false, updatable = false)
//    @JoinColumn(name = "rateable_id", insertable = false, updatable = false)
//    private Object rateable;

    @Column(columnDefinition = "TEXT")
    private String data;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


}
