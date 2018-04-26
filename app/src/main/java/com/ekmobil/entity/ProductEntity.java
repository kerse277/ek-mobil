package com.ekmobil.entity;

/**
 * Created by melih on 25.03.2018.
 */

public class ProductEntity {

    private String id;
    private String productName;
    private String productPrice;
    private String productImage;
    private String productUrl;
    private String productLike;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductLike() {
        return productLike;
    }

    public void setProductLike(String productLike) {
        this.productLike = productLike;
    }
}
