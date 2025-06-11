package com.neuedu.nep.io;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.neuedu.nep.entity.Member;



import java.io.*;

import java.net.URISyntaxException;

import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//统一使用类路径寻找
public class JsonIO {
    public static void writer(String filePath, Object obj) {
        File file= null;
        try {
            file = new File(JsonIO.class.getResource(filePath).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper mapper=new ObjectMapper();
        try (FileWriter writer = new FileWriter(file, true)) { // true 表示追加模式

            // 如果文件不存在或为空，写入数组起始符号
            if (!file.exists() || file.length() == 0) {
                writer.write("[");
            } else {
                // 如果文件已有内容，追加逗号分隔
                long length = file.length();
                if (length > 1) { // 确保文件至少有一个字符（可能是 [）
                    // 移除最后一个字符（即 ]）
                    RandomAccessFile raf = new RandomAccessFile(file, "rw");
                    raf.setLength(length - 1);
                    raf.close();

                    // 添加逗号分隔
                    writer.write(",");
                }
            }

            // 写入新对象的 JSON 文本
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            writer.write(json);

            // 写入数组结束符号
            writer.write("]");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static <T> List<T> read(String filePath, T t) {
//        Object obj=null;
        List<T> list = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.readTree(JsonIO.class.getResource(filePath));
            Iterator<JsonNode> iterator = tree.iterator();
            while (iterator.hasNext()) {
                JsonNode next = iterator.next();
                T temp = (T) mapper.treeToValue(next, t.getClass());
                list.add(temp);
            }
//            obj = list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
