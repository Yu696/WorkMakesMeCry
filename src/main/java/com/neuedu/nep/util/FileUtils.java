package com.neuedu.nep.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuedu.nep.entity.AQIData;
import com.neuedu.nep.entity.Member;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.neuedu.nep.io.JsonIO.read;

public class FileUtils {
    public static AQIData getThisPerson(String filePath, String feedbackpublisher) throws URISyntaxException {
        AQIData aqiData = new AQIData("1","广东省","广州市","天河区XX路XX号","一级","2025/6/11","空气质量良好，无明显污染","张三");
        List<AQIData> list = read(filePath, aqiData);
        for (AQIData a : list) {
            if (a.getPublisher().equals(feedbackpublisher)) {
                return a;
            }
        }
        System.out.println("没这个人--来自getthisperson");
        return null;
    }

    public static boolean registeredOrNot(String filePath, String aqiDatepublisher) {
        AQIData aqiData = new AQIData("1","广东省","广州市","天河区XX路XX号","一级","2025/6/11","空气质量良好，无明显污染","张三");
        List<AQIData> list = read(filePath, aqiData);
        System.out.println("名单读取成功");
        for (AQIData a : list) {
            System.out.println(a.toString());
            if (a.getPublisher().equals(aqiDatepublisher)) {

                    return true;

            }
        }return false;

    }

    public static AQIData findItAndGetIt(String filePath, String num) {
        List<AQIData> list = read(filePath, new AQIData());
        for (AQIData i : list) {
            if (i.getNum().equals(num)) {
                return i;
            }
        }
        System.out.println("未找到--来自finditandfetit");
        return null;
    }

    /**
     * 读取 AQIDataBaseCreatedBySup.Json 文件并找出最大序号
     * @return 最大序号，如果文件不存在或为空则返回 0
     */
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String JSON_FILE = "target/classes/dataBase/members/AQIDataBaseCreatedBySup.Json";

    public static int getMaxIdFromJson() {
        File file = new File(JSON_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("文件不存在或为空，最大序号返回 0");
            return 0;
        }
        try {
            List<AQIData> aqiDataList = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, AQIData.class));
            int maxId = 0;
            for (AQIData data : aqiDataList) {
                int id = Integer.parseInt(data.getNum());
                if (id > maxId) {
                    maxId = id;
                }
            }
            System.out.println("从文件中读取到的最大序号为: " + maxId);
            return maxId;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("读取文件出错，最大序号返回 0");
            return 0;
        }
    }
    /**
     * 读取 AQIDataBaseCreatedBySup.Json 文件中的所有 AQIData 数据
     * @return 所有 AQIData 数据列表
     */
    public static List<AQIData> readAllAqiData() {
        File file = new File(JSON_FILE);
        List<AQIData> aqiDataList = new ArrayList<>();
        if (file.exists() && file.length() > 0) {
            try {
                aqiDataList = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, AQIData.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return aqiDataList;
    }
}