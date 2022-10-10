package com.dynamic_validate.util;

import java.util.List;

public class ReadFuncInvoke implements DealLine {

    /**
     *功能：
     *      Java读取“函数调用txt文件”(eg：ring_file_publish.txt)的内容，
     *      返回调用关系(exp=“f1;f2”)的列表。
     */
    @Override
    public List<String> deal(String line, List<String> invocList, String... args) {
        String outerFunc = args[0];

        //if (line.startsWith(" ")) {
        //    String innerFunc = line.replaceAll(" |\\(|\\)", "");
        //    invocList.add(outerFunc + ";" + innerFunc);
        //    System.out.println("内部啦：" + innerFunc);
        //} else {
        //    outerFunc = line.split("\\(")[0];
        //    System.out.println("外部啦：" + outerFunc);
        //}
        return invocList;
    }
}
