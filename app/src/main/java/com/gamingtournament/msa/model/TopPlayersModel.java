package com.gamingtournament.msa.model;

public class TopPlayersModel {

    private String name, phno,imgurl;
    private int totalkill, totalmatch;

    public TopPlayersModel() {
    }

    public TopPlayersModel(String name, String phno, String imgurl, int totalkill, int totalmatch) {
        this.name = name;
        this.phno = phno;
        this.imgurl = imgurl;
        this.totalkill = totalkill;
        this.totalmatch = totalmatch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getTotalkill() {
        return totalkill;
    }

    public void setTotalkill(int totalkill) {
        this.totalkill = totalkill;
    }

    public int getTotalmatch() {
        return totalmatch;
    }

    public void setTotalmatch(int totalmatch) {
        this.totalmatch = totalmatch;
    }
}
