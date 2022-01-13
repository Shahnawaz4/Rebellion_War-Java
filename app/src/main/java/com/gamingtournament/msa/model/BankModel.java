package com.gamingtournament.msa.model;

public class BankModel {

    private String accnum;
    private String ifsc;
    private String accname;
    private int vercode;

    public BankModel() {
    }

    public BankModel(String accnum, String ifsc, String accname, int vercode) {
        this.accnum = accnum;
        this.ifsc = ifsc;
        this.accname = accname;
        this.vercode = vercode;
    }

    public String getAccnum() {
        return accnum;
    }

    public void setAccnum(String accnum) {
        this.accnum = accnum;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAccname() {
        return accname;
    }

    public void setAccname(String accname) {
        this.accname = accname;
    }

    public int getVercode() {
        return vercode;
    }

    public void setVercode(int vercode) {
        this.vercode = vercode;
    }
}
