package com.example.mymall.Model;

public class Users {
    private String fullname,email,ph_no,address,image;


    public Users(String fullname, String email, String ph_no, String address, String image) {
        this.fullname = fullname;
        this.email = email;
        this.ph_no = ph_no;
        this.address = address;
        this.image = image;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPh_no() {
        return ph_no;
    }

    public void setPh_no(String ph_no) {
        this.ph_no = ph_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Users()
    {

    }

}
