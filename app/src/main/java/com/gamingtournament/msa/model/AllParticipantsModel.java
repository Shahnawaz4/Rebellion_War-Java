package com.gamingtournament.msa.model;

public class AllParticipantsModel {

    private boolean iswinner;
    private String player1, player2, player3, player4;
    private String teamno, teamuid;
    private int teamkill,  player1kill, player2kill, player3kill,player4kill;

    public AllParticipantsModel() {
    }

    public AllParticipantsModel(boolean iswinner, String player1, String player2, String player3, String player4, String teamno, String teamuid,
                                int teamkill, int player1kill, int player2kill, int player3kill, int player4kill) {
        this.iswinner = iswinner;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
        this.teamno = teamno;
        this.teamuid = teamuid;
        this.teamkill = teamkill;
        this.player1kill = player1kill;
        this.player2kill = player2kill;
        this.player3kill = player3kill;
        this.player4kill = player4kill;
    }

    public boolean isIswinner() {
        return iswinner;
    }

    public void setIswinner(boolean iswinner) {
        this.iswinner = iswinner;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayer3() {
        return player3;
    }

    public void setPlayer3(String player3) {
        this.player3 = player3;
    }

    public String getPlayer4() {
        return player4;
    }

    public void setPlayer4(String player4) {
        this.player4 = player4;
    }

    public String getTeamno() {
        return teamno;
    }

    public void setTeamno(String teamno) {
        this.teamno = teamno;
    }

    public String getTeamuid() {
        return teamuid;
    }

    public void setTeamuid(String teamuid) {
        this.teamuid = teamuid;
    }

    public int getTeamkill() {
        return teamkill;
    }

    public void setTeamkill(int teamkill) {
        this.teamkill = teamkill;
    }

    public int getPlayer1kill() {
        return player1kill;
    }

    public void setPlayer1kill(int player1kill) {
        this.player1kill = player1kill;
    }

    public int getPlayer2kill() {
        return player2kill;
    }

    public void setPlayer2kill(int player2kill) {
        this.player2kill = player2kill;
    }

    public int getPlayer3kill() {
        return player3kill;
    }

    public void setPlayer3kill(int player3kill) {
        this.player3kill = player3kill;
    }

    public int getPlayer4kill() {
        return player4kill;
    }

    public void setPlayer4kill(int player4kill) {
        this.player4kill = player4kill;
    }
}
