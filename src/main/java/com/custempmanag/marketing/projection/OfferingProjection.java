package com.custempmanag.marketing.projection;


import java.math.BigDecimal;

public interface OfferingProjection {
    Long getId();
    String getName();
    BigDecimal getPrice();
    String getPath();
//    Double getRating();
//    void setPath(String path);
}
