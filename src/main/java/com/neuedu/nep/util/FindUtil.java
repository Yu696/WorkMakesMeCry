package com.neuedu.nep.util;

import com.neuedu.nep.entity.AQIData;
import com.neuedu.nep.entity.Member;
import javafx.collections.ObservableList;

import java.net.URISyntaxException;
import java.util.List;

import static com.neuedu.nep.io.JsonIO.read;
import static com.neuedu.nep.util.AlertUtils.showAlert;

public class FindUtil {
    public static Member getThisPerson(String filePath, String account) throws URISyntaxException {
        Member member=new Member("余润东","男","111","123");
        List<Member> list=read(filePath,member);
        for(Member a : list){
            if(a.getAccount().equals(account)){
                return a;
            }
        }
        return null;
    }

    public static boolean registeredOrNot(String filePath, String memberAccount,String passWord) {
        Member member=new Member("余润东","男","111","123");
        List<Member> memberList=read(filePath,member);
        for(Member a : memberList){
            if(a.getAccount().equals(memberAccount) ){
                if(a.getPassWord().equals(passWord)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public static AQIData findItAndGetIt(String filePath, String num){
        List<AQIData> list=read(filePath,new AQIData());
        for(AQIData i : list){
            if (i.getNum().equals(num)){
                return i;
            }
        }
        return null;
    }
}
