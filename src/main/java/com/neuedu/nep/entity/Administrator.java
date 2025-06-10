package com.neuedu.nep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Administrator extends Member  {
    @JsonIgnore
    private int maxElementIndexForInsert;
    public Administrator() {
    }

    public Administrator(String name, String sex, String account, String passWord) {
        super(name, sex, account, passWord);
    }

    @Override
    public String toString() {
        return "Administer{}";
    }



}
