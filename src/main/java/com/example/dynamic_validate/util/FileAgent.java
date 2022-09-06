package com.example.dynamic_validate.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

interface DealLine {
    List<String> deal(String line, List<String> list, String... args);
}

public class FileAgent implements DealLine {
    DealLine dealObj;

    public FileAgent() {
    }

    public FileAgent(DealLine dealObj) {
        this.dealObj = dealObj;
    }

    /**
     * 功能：
     *      读文件，根据不同的 deal值, 要求返回不同的 List<String>
     *
     *      不喜欢用if-else，难道要用 代理模式 吗？
     *
     * 步骤：
     * 1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     */
    public List<String> readTxtFile(String filePath) {
        List<String> invocList = new ArrayList<String>();
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                String outerFunc = "";
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    invocList = deal(lineTxt, invocList, outerFunc);
                }
                read.close();
                return invocList;
            } else {
                System.out.println("找不到指定的文件");
                return null;
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> deal(String line, List<String> list, String... args) {
        return dealObj.deal(line, list);
    }

}