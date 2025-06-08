package com.neuedu.nep.entity;

public class Gridder extends Member{
    public Gridder() {
    }

    public Gridder(String name, String sex, String account, String passWord) {
        super(name, sex, account, passWord);
    }

    @Override
    public String toString() {
        return "Gridder{}";
    }
}
