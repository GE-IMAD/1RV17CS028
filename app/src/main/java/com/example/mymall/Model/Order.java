package com.example.mymall.Model;

import java.util.HashMap;

public class Order {


    private String Order_id;
    private HashMap<String,String> ProductMap;

    public Order() {
    }

    public Order(String order_id, HashMap<String, String> productMap) {
        Order_id = order_id;
        ProductMap = productMap;
    }

    public String getOrder_id() {
        return Order_id;
    }

    public void setOrder_id(String order_id) {
        Order_id = order_id;
    }

    public HashMap<String, String> getProductMap() {
        return ProductMap;
    }

    public void setProductMap(HashMap<String, String> productMap) {
        ProductMap = productMap;
    }
}
