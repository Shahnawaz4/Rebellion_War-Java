package com.gamingtournament.msa.model;

public class UserInformation {

    private String name, email, password, phno, imgurl, uid;
    private int totalmatch, totalkill, balance, bonuspoint;

    public UserInformation(){

    }

    public UserInformation(String name, String email, String password, String phno, String imgurl, String uid,
                           int totalmatch, int totalkill, int balance, int bonuspoint) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phno = phno;
        this.imgurl = imgurl;
        this.uid = uid;
        this.totalmatch = totalmatch;
        this.totalkill = totalkill;
        this.balance = balance;
        this.bonuspoint = bonuspoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getTotalmatch() {
        return totalmatch;
    }

    public void setTotalmatch(int totalmatch) {
        this.totalmatch = totalmatch;
    }

    public int getTotalkill() {
        return totalkill;
    }

    public void setTotalkill(int totalkill) {
        this.totalkill = totalkill;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBonuspoint() {
        return bonuspoint;
    }

    public void setBonuspoint(int bonuspoint) {
        this.bonuspoint = bonuspoint;
    }
}
