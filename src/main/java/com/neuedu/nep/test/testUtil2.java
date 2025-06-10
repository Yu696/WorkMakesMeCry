package com.neuedu.nep.test;

import static com.neuedu.nep.util.AlertUtils.registeredOrNot;

public class testUtil2 {
    public static void main(String[] args) {
        boolean ss=registeredOrNot("/dataBase/members/supervisor.Json","111");
        System.out.println(ss);


    }
}
