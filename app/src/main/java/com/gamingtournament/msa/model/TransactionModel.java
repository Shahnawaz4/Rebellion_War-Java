package com.gamingtournament.msa.model;

public class TransactionModel {

    private String status, id, txnid;
    private int bal;

    public TransactionModel() {
    }

    public TransactionModel(String status, String id, String txnid, int bal) {
        this.status = status;
        this.id = id;
        this.txnid = txnid;
        this.bal = bal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTxnid() {
        return txnid;
    }

    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }

    public int getBal() {
        return bal;
    }

    public void setBal(int bal) {
        this.bal = bal;
    }
}
