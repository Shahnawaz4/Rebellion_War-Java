package com.gamingtournament.msa.model;

public class UpdateModel {

    private String msg;
    private String ver;
    private boolean isman;

    public UpdateModel() {
    }


    public UpdateModel(String msg, String ver, boolean isman) {
        this.msg = msg;
        this.ver = ver;
        this.isman = isman;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public boolean isIsman() {
        return isman;
    }

    public void setIsman(boolean isman) {
        this.isman = isman;
    }
}
