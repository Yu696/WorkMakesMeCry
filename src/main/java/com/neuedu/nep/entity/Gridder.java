package com.neuedu.nep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Gridder extends Member  {
    @JsonIgnore
    private int maxElementIndexForInsert;
    public Gridder() {
    }

    public Gridder(String name, String sex, String account, String passWord,String state) {
        super(name, sex, account, passWord,state);
    }

    @Override
    public String toString() {
        return super.toString() + "Gridder{" + '}';
    }

    public String showInfo(){
        return this.getName()+" "+this.getSex()+" 账号:"+this.getAccount()+" 现在的任务："+this.getState();
    }

}
