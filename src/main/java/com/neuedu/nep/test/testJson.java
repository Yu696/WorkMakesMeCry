package com.neuedu.nep.test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuedu.nep.entity.Member;
import com.neuedu.nep.entity.Supervisor;

import java.io.IOException;
import java.net.URISyntaxException;



public class testJson {


    public static void main(String[] args) throws JsonProcessingException {
        Supervisor supervisor=new Supervisor("yu","sd","111","111","111");
        ObjectMapper mapper = new ObjectMapper();

// 启用类型信息（与反序列化配置一致）
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );



        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(supervisor);
        System.out.println("序列化后的 JSON:");
        System.out.println(json);

        Member deserialized = mapper.readValue(json, Member.class);
        System.out.println("反序列化后的类型: " + deserialized.getClass().getName());
        System.out.println(deserialized.toString());
    }
}
