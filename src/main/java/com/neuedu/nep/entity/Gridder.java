package com.neuedu.nep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Gridder extends Member  {
    @JsonIgnore
    private int maxElementIndexForInsert;
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
