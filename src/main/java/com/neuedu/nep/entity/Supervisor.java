package com.neuedu.nep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Supervisor extends Member  {
    @JsonIgnore
    private int maxElementIndexForInsert;


    private String state;

    public Supervisor(String name, String sex, String account,String passWord, String state) {
        super(name, sex, account,passWord);
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

