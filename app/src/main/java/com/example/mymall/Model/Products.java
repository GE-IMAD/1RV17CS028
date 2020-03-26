package com.example.mymall.Model;

public class Products {
    private String description,price,Name,date,pid,pimg,quantity,SoldBy;
    public Products()
    {}

    public String getSoldBy() {
        return SoldBy;
    }

    public void setSoldBy(String soldBy) {
        SoldBy = soldBy;
    }

    public Products(String description, String price, String name, String date, String pid, String pimg, String quantity, String SoldBy) {
        this.description = description;
        this.price = price;
        Name = name;
        this.date = date;
        this.pid = pid;
        this.pimg = pimg;
        this.quantity=quantity;
        this.SoldBy=SoldBy;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPimg(String pimg) {
        this.pimg = pimg;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return Name;
    }

    public String getDate() {
        return date;
    }

    public String getPid() {
        return pid;
    }

    public String getPimg() {
        return pimg;
    }
}
