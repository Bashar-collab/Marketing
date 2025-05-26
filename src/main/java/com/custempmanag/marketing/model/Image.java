package com.custempmanag.marketing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    private Long imageableId;

    private String imageableType;

    public Image() {
    }

    public Image(Long id, String path) {
        this.id = id;
        this.path = path;
    }
}
