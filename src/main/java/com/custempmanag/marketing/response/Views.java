package com.custempmanag.marketing.response;

public class Views {
    public interface Public {} // For common fields (all users)
    public interface Admin {} // For restricted fields (Admin requesters only)
}
