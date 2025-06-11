package com.neuedu.nep.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.neuedu.nep.entity.Member;
import com.neuedu.nep.entity.Supervisor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.neuedu.nep.io.JsonIO.*;

//目前还没有解决子类与父类间不能存在同一个列表中的情况，需要用json的特有声明方法
public class testIO {
    public static void main(String[] args) {
        Member member1 = new Member("余润东", "男", "111", "123");
        Member member2 = new Member("余润东", "男", "123", "111");
        Member member3 = new Member("余润东", "男", "111", "123");
        Supervisor member4 = new Supervisor("蔡依林", "女", "142", "1556", "free");
        Supervisor supervisor = new Supervisor("陈梓轩", "男", "250", "1455", "监管者");
        writer("/dataBase/members/supervisor.Json", member4);
        List<Member> memberList = read("/dataBase/members/supervisor.Json", member1);
        for (Member member : memberList) {

            System.out.println(member.toString());
        }

    }
}

