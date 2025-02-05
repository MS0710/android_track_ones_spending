package com.example.accountappv2.data;

public class UserInfo {
    private String account;
    private String password;
    private String name;
    public UserInfo(String _account,String _password,String _name){
        this.account = _account;
        this.password = _password;
        this.name = _name;
    }

    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

}
