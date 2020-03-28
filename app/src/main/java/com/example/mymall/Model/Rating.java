package com.example.mymall.Model;

public class Rating {
   private String num,den;

    public Rating() {
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDen() {
        return den;
    }

    public void setDen(String den) {
        this.den = den;
    }

    public Rating(String num, String den) {
        this.num = num;
        this.den = den;
    }
}
