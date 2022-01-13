package com.gamingtournament.msa.model;

public class GameModel {

    private String header, date, time, map, persp, roomid, roompass, game, gametype, refid, wish;
    private int  winner, perkill, entryfee, playerjoined, totalplayer;
    private boolean iscompleted;
    private String youtubeurl, by;

    public GameModel() {
    }

    public GameModel(String header, String date, String time, String map, String persp, String roomid, String roompass, String game, String gametype, String refid, String wish,
                     int winner, int perkill, int entryfee, int playerjoined, int totalplayer, boolean iscompleted, String youtubeurl, String by) {
        this.header = header;
        this.date = date;
        this.time = time;
        this.map = map;
        this.persp = persp;
        this.roomid = roomid;
        this.roompass = roompass;
        this.game = game;
        this.gametype = gametype;
        this.refid = refid;
        this.wish = wish;
        this.winner = winner;
        this.perkill = perkill;
        this.entryfee = entryfee;
        this.playerjoined = playerjoined;
        this.totalplayer = totalplayer;
        this.iscompleted = iscompleted;
        this.youtubeurl = youtubeurl;
        this.by = by;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getPersp() {
        return persp;
    }

    public void setPersp(String persp) {
        this.persp = persp;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getRoompass() {
        return roompass;
    }

    public void setRoompass(String roompass) {
        this.roompass = roompass;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getGametype() {
        return gametype;
    }

    public void setGametype(String gametype) {
        this.gametype = gametype;
    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public String getWish() {
        return wish;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getPerkill() {
        return perkill;
    }

    public void setPerkill(int perkill) {
        this.perkill = perkill;
    }

    public int getEntryfee() {
        return entryfee;
    }

    public void setEntryfee(int entryfee) {
        this.entryfee = entryfee;
    }

    public int getPlayerjoined() {
        return playerjoined;
    }

    public void setPlayerjoined(int playerjoined) {
        this.playerjoined = playerjoined;
    }

    public int getTotalplayer() {
        return totalplayer;
    }

    public void setTotalplayer(int totalplayer) {
        this.totalplayer = totalplayer;
    }

    public boolean isIscompleted() {
        return iscompleted;
    }

    public void setIscompleted(boolean iscompleted) {
        this.iscompleted = iscompleted;
    }

    public String getYoutubeurl() {
        return youtubeurl;
    }

    public void setYoutubeurl(String youtubeurl) {
        this.youtubeurl = youtubeurl;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }
}
