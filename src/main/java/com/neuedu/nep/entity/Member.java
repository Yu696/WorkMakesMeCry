package com.neuedu.nep.entity;

public class Member {
    private String name;
    private String sex;
    private String code;
    private String account;
    private String passWord;
    public Member() {
    }

    public Member(String name, String sex, String code, String account,String passWord) {
        this.name = name;
        this.sex = sex;
        this.code = code;
        this.account = account;
        this.passWord=passWord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", code='" + code + '\'' +
                ", account='" + account + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}