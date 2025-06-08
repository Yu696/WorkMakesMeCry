package com.neuedu.nep.entity;

public class Member {
    private String name;
    private String sex;
    private String account;
    private String passWord;
    public Member() {
    }

    public Member(String name, String sex, String account,String passWord) {
        this.name = name;
        this.sex = sex;
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
                ", account='" + account + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}