package com.neuedu.nep.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JacksonIO {
    public static boolean write(String filePath, Object object) throws IOException {
        ObjectMapper mapper=new ObjectMapper();
        if(object==null){
            throw new IOException("对象不存在！");
        }
        try {
            mapper.writeValue(new File(filePath), object);
            return true;
        } catch (IOException e) {
            System.out.println("Filed to write JSON File: "+e.getMessage());
            return false;
        }
    }

    public static <T> List<T> read(String fileName,T t){
        Object o=null;
        List<T> list=new ArrayList<>();
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode tree= mapper.readTree(new File(fileName));
            Iterator<JsonNode> iterator= tree.iterator();
            while(iterator.hasNext()){
                JsonNode next=iterator.next();
                T temp=(T)mapper.treeToValue(next,t.getClass());
                list.add(temp);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}