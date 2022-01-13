package com.gamingtournament.msa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeISTModel {

    // website name : worldtimeapi.org

    @SerializedName("abbreviation")
    @Expose
    private String abbreviation;

    @SerializedName("datetime")
    @Expose
    private String datetime;

    @SerializedName("day_of_week")
    @Expose
    private int day_of_week;

    // return time(in second) diff between UTC and IST
    @SerializedName("raw_offset")
    @Expose
    private int raw_offset;

    @SerializedName("timezone")
    @Expose
    private String timezone;

    @SerializedName("unixtime")
    @Expose
    private int unixtime;

    @SerializedName("utc_datetime")
    @Expose
    private String utc_datetime;


    // getter and setter
    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(int day_of_week) {
        this.day_of_week = day_of_week;
    }

    public int getRaw_offset() {
        return raw_offset;
    }

    public void setRaw_offset(int raw_offset) {
        this.raw_offset = raw_offset;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getUnixtime() {
        return unixtime;
    }

    public void setUnixtime(int unixtime) {
        this.unixtime = unixtime;
    }

    public String getUtc_datetime() {
        return utc_datetime;
    }

    public void setUtc_datetime(String utc_datetime) {
        this.utc_datetime = utc_datetime;
    }
}
