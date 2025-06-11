package com.neuedu.nep.util;

import com.neuedu.nep.entity.Member;

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
        System.out.println("没这个人--来自getthisperson");
        return null;
    }

    public static boolean registeredOrNot(String filePath, String memberAccount,String passWord) {
        Member member=new Member("余润东","男","111","123");
        List<Member> memberList=read(filePath,member);
        System.out.println("名单读取成功");
        for(Member a : memberList){
            System.out.println(a.toString());
            if(a.getAccount().equals(memberAccount) ){
                if(a.getPassWord().equals(passWord)) {
                    return true;
                }
                return false;
            }
        }
        return false;
        //            JsonMapper jsonMapper = new JsonMapper();
//            JsonNode jsonNode = jsonMapper.readTree(JsonIO.class.getResource(filePath));
//            System.out.println(jsonNode.asText());
//            if (jsonNode.isArray()) {
//                for (JsonNode jsonItem : jsonNode) {
//                    if (jsonItem.get("account").asText().equals(memberAccount)) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//            if (jsonNode.isObject()) {
//                if (jsonNode.get("account").asText().equals(memberAccount)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//            if (jsonNode.isEmpty()){
//                return false;
//            }
//            else {
//                System.out.println("数据格式不正确");
//                return true;
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}
