package com.neuedu.nep.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Supervisor.class, name = "supervisor"),
        @JsonSubTypes.Type(value = Administrator.class, name = "administrator"),
        @JsonSubTypes.Type(value = Gridder.class, name = "gridder")
})

@JsonIgnoreProperties({ "maxElementIndexForInsert", "MemberPath" })
public class Member extends JsonNodeFactory {
    private String name;
    private String sex;
    private String account;
    private String passWord;
    public Member() {
    }

    public Member(String account,String passWord){
        this.account=account;
        this.passWord=passWord;
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