package com.example.accountappv2.data;

public class ReceiptItem {
    private int id;
    private String detail;

    public ReceiptItem(int _id,String _detail){
        this.id = _id;
        this.detail = _detail;
    }

    public int getId() {
        return id;
    }

    public String getDetail() {
        return detail;
    }
}
