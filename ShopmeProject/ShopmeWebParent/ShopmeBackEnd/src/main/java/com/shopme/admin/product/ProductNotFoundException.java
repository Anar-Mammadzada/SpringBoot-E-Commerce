package com.shopme.admin.product;

public class ProductNotFoundException extends Throwable {
    public ProductNotFoundException(String s) {
        super(s);
    }
}