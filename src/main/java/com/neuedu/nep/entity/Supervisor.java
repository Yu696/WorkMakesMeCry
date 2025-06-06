package com.neuedu.nep.entity;

public class Supervisor extends Member{
    private String state;

    public Supervisor(String name, String sex, String code, String account,String passsWord, String state) {
        super(name, sex, code, account,passsWord);
        this.state = state;
    }

    public Supervisor() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return super.toString() + "Supervisor{" +
                "state='" + state + '\'' +
                '}';
    }
}

