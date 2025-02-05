package com.example.accountappv2.data;

public class BillData {
    private String keyId;
    private String year;
    private String month;
    private String day;
    private String type;
    private String detail;
    private String cost;

    public BillData(String _keyId,String _year,String _month,String _day,String _type,String _detail,String _cost){
        this.keyId = _keyId;
        this.year = _year;
        this.month = _month;
        this.day = _day;
        this.type = _type;
        this.detail = _detail;
        this.cost = _cost;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getType() {
        return type;
    }

    public String getDetail() {
        return detail;
    }

    public String getCost() {
        return cost;
    }
}
