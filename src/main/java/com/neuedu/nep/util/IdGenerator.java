package com.neuedu.nep.util;

import java.io.*;

public class IdGenerator {
    private static final String ID_FILE = "feedback_id.txt";

    public synchronized static long generateNextId() {
        long currentId = loadId();
        saveId(currentId + 1);
        return currentId + 1;
    }

    private static long loadId() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ID_FILE))) {
            return Long.parseLong(reader.readLine());
        } catch (Exception e) {
            saveId(0);
            return 0;
        }
    }

    private static void saveId(long id) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ID_FILE))) {
            writer.write(String.valueOf(id));
        } catch (IOException e) {
            throw new RuntimeException("序号保存失败: " + e.getMessage());
        }
    }
}