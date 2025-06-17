package com.neuedu.nep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Supervisor extends Member  {
    @JsonIgnore
    private int maxElementIndexForInsert;


    public Supervisor() {
    }

    public Supervisor(String name, String sex, String account, String passWord,String state) {
        super(name, sex, account, passWord,state);
    }

    @Override
    public String toString() {
        return super.toString() + "Supervisor{" +
                '}';
    }
    public String showInfo(){
        return this.getName()+" "+this.getSex()+" 账号:"+this.getAccount()+" 现在的任务："+this.getState();
    }
}

