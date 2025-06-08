package com.neuedu.nep.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.neuedu.nep.entity.Member;
import javafx.scene.shape.Path;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
//统一使用类路径寻找
public class JsonIO {
    public static boolean writeAMember(String filePath, Member member) throws IOException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
        if (JsonIO.class.getResource(filePath) == null) {
            System.out.println("资源不存在:" + filePath);
            return false;
        } else {
            System.out.println("正向："+filePath+" 写入"+member.toString());
            URL resourceUrl = JsonIO.class.getResource(filePath);
            File file=new File(resourceUrl.toURI());
            List<Member> list=read(filePath, Member.class);
            list.add(member);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file,list);
            System.out.println("成功写入");
            }
            return true;
        }


    //统一使用类路径寻找
    public static <T> List<T> read(String filePath,Class<T> t)throws FileNotFoundException {
        JsonMapper mapper = new JsonMapper();
        try (InputStream is = JsonIO.class.getResourceAsStream(filePath)) {
            if (is == null) {
                System.out.println("资源不存在：" + filePath);
                return null;
            } else {
                String resources=new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JsonNode root = mapper.readTree(resources);
                if (root.isArray()) {
                    return mapper.readValue(resources, new TypeReference<List<T>>() {});
                }
                if (root.isObject()) {
                    List<String> list=new ArrayList<>();
                    list.add(resources);
                    return (List<T>) list;
                }
                else {
                    System.out.println("文件无法解析，请查看文件格式");
                    return null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}