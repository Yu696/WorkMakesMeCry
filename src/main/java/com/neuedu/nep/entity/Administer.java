package com.neuedu.nep.entity;
import java.util.ArrayList;
import java.util.List;


public class Administer extends Member{

    public Administer() {
    }

    public Administer(String name, String sex, String account, String passWord) {
        super(name, sex, account, passWord);
    }

    @Override
    public String toString() {
        return "Administer{}";
    }
}
