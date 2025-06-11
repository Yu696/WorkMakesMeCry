package com.neuedu.nep.test;

import static com.neuedu.nep.util.FindUtil.registeredOrNot;

public class testUtil2 {
    public static void main(String[] args) {
        boolean ss=registeredOrNot("/dataBase/members/supervisor.Json","111","123");
        System.out.println(ss);


    }
}
