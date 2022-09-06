package com.example.dynamic_validate.util;

import com.example.dynamic_validate.data.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    /**
     * 给定目录的完整path，
     *
     * 读取目录下的所有文件名，没有path，纯文件名。
     */
    public static String[] getFilesName(String dentryPath) {
        File file = new File(dentryPath); //需要获取的文件的路径
        String[] fileNameLists = file.list(); //存储文件名的String数组
        //File[] filePathLists = file.listFiles(); //存储文件路径的String数组
        return fileNameLists;
    }

    /**
     * 1, 读取调用关系
     *      Java读取“函数调用txt文件”(eg：ring_file_publish.txt)的内容，
     *      返回调用关系(exp=“f1;f2”)的列表。
     */
    public static List<String> readFuncInvoke(String filePath) {
        List<String> invocList = new ArrayList<String>();
        try {
            String encoding = "UTF-8";

            File file = new File(filePath);
            if (filePath.endsWith(".txt") && file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);

                String lineTxt = null;
                int tab = 4;
                int depth;
                //String[] curFather = new String[30];
                List<String> curFather = new ArrayList<>();
                String name;
                String regMacro = "^\\s*[_A-Z0-9]+\\(?\\)?";
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.startsWith("\n")) {
                        //oldOuter = outerFunc;
                        //outerFunc = lineTxt.split("\\(")[0];
                        System.out.println("匹配空行");
                    } else {
                        if (!lineTxt.startsWith(" ") && lineTxt.matches(".*>:?$")) {
                            name = lineTxt.split("\\(")[0];
                            System.out.println("当前文件全局函数：" + name);
                            depth = 0;
                        } else {
                            int blankNums = lineTxt.split("[A-Za-z_]+.*")[0].length();
                            depth = blankNums / tab;
                            name = lineTxt.replaceAll(" |\\(|\\)|<.*>:?$", "");
                        }

                        if (curFather.size() <= depth) {
                            curFather.add(depth, name);//由于宏跟本就不会往里走，所以也不用纠结不添加宏的情况了。
                        } else {
                            curFather.set(depth, name);
                        }

                        if (depth != 0) {
                            if (!lineTxt.matches(regMacro) && lineTxt.contains("()")) {
                                invocList.add(curFather.get(depth - 1) + ";" + name); // 这个outer没有更新到最里层！
                            }
                            System.out.println(curFather.get(depth - 1) + ";" + name);
                        }
                    }
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

    /**
     * 功能：
     *      读文件，根据不同的 deal值, 要求返回不同的 List<String>
     *             1, 读取调用关系
     *             2, readFuncDef
     *             3, generLevelTxt
     *             4, 把上面的几种，直接读到库中，不需要转为Excel这一步了【读到Excel里面】
     *                  【level|path|exp】就需要这三个字段
     *                  按文件名读入，一行行按照level写入type表即可。
     *
     *             5, 把Excel导入成为数据库的exp，然后再在数据库中解析。【暂时不弄，需要再说】
     *                  细分：static，
     *
     *
     *      不喜欢用if-else，难道要用 代理模式 吗？【瞎弄，不成了，删了吧】
     *
     *————————以上：——————
     *
     * 功能：
     *      Java读取“函数调用txt文件”(eg：ring_file_publish.txt)的内容，
     *      返回调用关系(exp=“f1;f2”)的列表。
     *
     * 步骤：
     * 1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     */
    public static List<String> readTypeList(String filePath) {
        List<String> typeList = new ArrayList<String>();
        try {
            String encoding = "UTF-8";
            String f2 = "_func_def.txt";
            String f3 = "_struct.txt";
            String f8 = "_include.txt";
            String f9 = "_func_declare.txt";
            String f13 = "_typedef.txt";
            String f14 = "_struct_declare.txt";
            String f12 = "_vars.txt";

            File file = new File(filePath);

            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);

                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    String str = "";
                    if (filePath.contains(f2)) { //函数定义的文件
                        if (StrUtil.funcIgnoreWithStar(lineTxt).split("\\(")[0].contains(" ")) { // 忽略词后，宏括号前只有一个单词，而函数至少有两个单词
                            str = "2|" + filePath.replaceAll(".txt" + f2, "") + "|" + lineTxt;
                        } else {
                            str = "7|" + filePath.replaceAll(".txt" + f2, "") + "|" + lineTxt;
                        }
                    } else if (filePath.contains(f3)) { //结构体定义的文件
                        if (lineTxt.matches("^struct[\\w\\s]+=\\s*\\{[^;]*\\};\\s*$")) { // 结构体变量初始化
                            str = "12|" + filePath.replaceAll(".txt" + f3, "") + "|" + lineTxt;
                        } else if (lineTxt.matches("^typedef.*")) { // typedef结构体别名定义
                            str = "13|" + filePath.replaceAll(".txt" + f3, "") + "|" + lineTxt;
                        } else if (lineTxt.matches("^enum.*")) {
                            str = "10|" + filePath.replaceAll(".txt" + f3, "") + "|" + lineTxt;
                        } else if (lineTxt.matches("^union.*")) {
                            str = "11|" + filePath.replaceAll(".txt" + f3, "") + "|" + lineTxt;
                        } else {
                            str = "3|" + filePath.replaceAll(".txt" + f3, "") + "|" + lineTxt;
                        }
                    } else if (filePath.contains(f8)) { //include的文件
                        str = "8|" + filePath.replaceAll(".txt" + f8, "") + "|" + lineTxt;
                    } else if (filePath.contains(f9)) { //函数声明的文件，包括typedef的函数，要摘出来！
                        if (lineTxt.startsWith("typedef")) {
                            str = "13|" + filePath.replaceAll(".txt" + f9, "") + "|" + lineTxt;
                        } else if (StrUtil.funcIgnoreWithStar(lineTxt).split("\\(")[0].contains(" ")) { // 函数声明，而非宏
                            str = "9|" + filePath.replaceAll(".txt" + f9, "") + "|" + lineTxt;
                        } else {
                            str = "7|" + filePath.replaceAll(".txt" + f9, "") + "|" + lineTxt;
                        }
                    } else if (filePath.contains(f14)) { //结构体声明的文件
                        if (lineTxt.startsWith("typedef")) {
                            str = "13|" + filePath.replaceAll(".txt" + f14, "") + "|" + lineTxt;
                        } else {
                            str = "14|" + filePath.replaceAll(".txt" + f14, "") + "|" + lineTxt;
                        }
                    } else if (filePath.contains(f13)) { //typedef声明的文件
                        str = "13|" + filePath.replaceAll(".txt" + f13, "") + "|" + lineTxt;
                    } else if (filePath.contains(f12)) { //变量声明的文件
                        str = "12|" + filePath.replaceAll(".txt" + f12, "") + "|" + lineTxt;
                    }
                    typeList.add(str);
                }
                read.close();
                return typeList;
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

    /**
     * 3, 二次处理file_name.txt：分开include，函数定义(括号结束)，函数声明(分号结束)，大写的宏【扔出去】
     *                  _func_def.txt，level=2，path=文件
     *                  _struct.txt，level=3，path=文件【从中再提取成员函数】
     *                  _include.txt，level=8，path=文件
     *                  _func_declare.txt，level=9，path=文件
     *                  _struct_declare.txt，level=14，path=文件
     *                  _vars.txt，level=12，path=文件
     *                  _typedef.txt，level=13，path=文件
     */
    public static void distribLevelTxt(String filePath) {
        try {
            String encoding = "UTF-8";
            String f2 = "_func_def.txt";
            String f3 = "_struct.txt";
            String f8 = "_include.txt";
            String f9 = "_func_declare.txt";
            String f13 = "_typedef.txt";
            String f14 = "_struct_declare.txt";
            String f12 = "_vars.txt";

            File file = new File(filePath);
            FileOutputStream o2 = new FileOutputStream(filePath + f2);
            FileOutputStream o3 = new FileOutputStream(filePath + f3);
            FileOutputStream o8 = new FileOutputStream(filePath + f8);
            FileOutputStream o9 = new FileOutputStream(filePath + f9);
            FileOutputStream o12 = new FileOutputStream(filePath + f12);
            FileOutputStream o13 = new FileOutputStream(filePath + f13);
            FileOutputStream o14 = new FileOutputStream(filePath + f14);


            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);

                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    lineTxt = lineTxt.replaceAll("\\s", " ");
                    if (lineTxt.equals("")) continue;
                    lineTxt = lineTxt.replaceAll(Data.structPreIgnoreWord, "").trim(); // 忽略最前面的extern和const
                    if (lineTxt.matches("(^struct.*\\{\\s*$)|(^enum.*\\{\\s*$)|(^union.*\\{\\s*$)")) {
                        int num = 1;
                        o3.write(lineTxt.getBytes());
                        while ((lineTxt = bufferedReader.readLine()) != null) {
                            lineTxt = lineTxt.replaceAll("\\s", " ");

                            if (lineTxt.startsWith("}")) {
                                o3.write("};".getBytes());
                                num--;
                            } else if (lineTxt.matches(".+}.*;\\s*$")) {
                                if (!lineTxt.startsWith("typedef")) {
                                    lineTxt = lineTxt.replaceAll("}.*;\\s*$", "};");
                                }
                                o3.write(lineTxt.getBytes());
                                num--;
                            } else {
                                o3.write(lineTxt.getBytes());
                            }
                            if (num == 0) {
                                o3.write(Data.WinNewLine.getBytes());
                                break;
                            }
                            if (lineTxt.matches("(^struct.*\\{\\s*$)|(^enum.*\\{\\s*$)|(^union.*\\{\\s*$)"))
                                num++;
                        }
                    } else if (lineTxt.matches("(^typedef struct.*\\{\\s*$)|(^typedef enum.*\\{\\s*$)|(^typedef union.*\\{\\s*$)")) {
                        int num = 1;
                        o3.write(lineTxt.getBytes());
                        while ((lineTxt = bufferedReader.readLine()) != null) {
                            lineTxt = lineTxt.replaceAll("\\s", " ");

                            if (lineTxt.startsWith("}")) {
                                //o3.write("};".getBytes());
                                o3.write(lineTxt.getBytes());
                                num--;
                            } else if (lineTxt.matches(".+}.*;\\s*$")) {
                                //if (!lineTxt.startsWith("typedef")) { // typedef的别名不要替换掉
                                //    lineTxt = lineTxt.replaceAll("}.*;\\s*$", "};");
                                //}
                                o3.write(lineTxt.getBytes());
                                num--;
                            } else {
                                o3.write(lineTxt.getBytes());
                            }
                            if (num == 0) {
                                o3.write(Data.WinNewLine.getBytes());
                                break;
                            }
                            if (lineTxt.matches("(^typedef struct.*\\{\\s*$)|(^typedef enum.*\\{\\s*$)|(^typedef union.*\\{\\s*$)"))
                                num++;
                        }
                    } else if (lineTxt.matches("(^struct.*\\};\\s*$)|(^enum.*\\};\\s*$)|(^union.*\\};\\s*$)|(^typedef struct.*\\};\\s*$)|(^typedef enum.*\\};\\s*$)|(^typedef union.*\\};\\s*$)")) {
                        o3.write((lineTxt + Data.WinNewLine).getBytes());
                    } else if (lineTxt.startsWith("#")) {
                        continue;
                    } else if (lineTxt.matches(".*\\(.*\\)((\\s*$)|(\\s*\\{.*\\}\\s*$))")) { // 函数定义，1 以)结束的是函数 2 是函数体为{}的情况
                        lineTxt = lineTxt.replaceAll("\\{.*\\}", " ");
                        o2.write((lineTxt + Data.WinNewLine).getBytes());
                    } else if (lineTxt.matches(".*\\(.*\\);\\s*$")) {// 函数声明，包括typedef的函数
                        o9.write((lineTxt + Data.WinNewLine).getBytes());
                    } else if (lineTxt.endsWith(".h")) {
                        o8.write((lineTxt + Data.WinNewLine).getBytes());
                    } else if (lineTxt.startsWith("struct") && lineTxt.trim().split(" ").length == 2) { // 结构体声明，两个单词长度。
                        o14.write((lineTxt + Data.WinNewLine).getBytes());
                    } else if (lineTxt.startsWith("typedef")) { // typedef的情况，主要针对别名。结构体和函数分别在其文件中。
                        o13.write((lineTxt + Data.WinNewLine).getBytes());
                    } else {//剩下就是变量了呗
                        //if (lineTxt.startsWith("#")) continue;
                        o12.write((lineTxt + Data.WinNewLine).getBytes());
                    }
                }
                o2.close();
                o3.close();
                o8.close();
                o9.close();
                o12.close();
                o14.close();
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

    /**
     * 2, 剔除空行，注释，#ifdef和 #endif之间的，include，写到新文件 file_name.txt中
     *                读取include_list。
     *
     *
     * 一行行读取文件，如果下一行是左大括号“{”，
     * 获取本行及上一行空行之间的数据，并且去掉换行符，合成一行。
     *
     * 例子：
     *    int finish_open(struct file *file, struct dentry *dentry,
     *    int (*open)(struct inode *, struct file *),
     *    int *opened)
     *    {xxx}
     */
    public static void readFuncDef(String filePath) {
        try {
            String encoding = "UTF-8";

            File file = new File(filePath);
            File result_file = new File(filePath + ".txt");
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(result_file), encoding);//考虑到编码格式
            BufferedWriter out = new BufferedWriter(write);
            FileOutputStream o31 = new FileOutputStream(filePath + "_can't_deal.txt");

            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);

                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    lineTxt = lineTxt.trim();
                    if (lineTxt.equals("") || lineTxt.matches("(^\\s*/{2}.*)|(^\\s*/?\\*.*)")) { // 匹配 1 以任意空格+//或/或*开头的行，就是/**/和//注释 2 以*/结尾的
                        if (lineTxt.matches("(^\\s*/\\*.*)") && !lineTxt.matches("(.*\\*/\\s*$)")) { // 以/*开头 却不以*/结尾。
                            while ((lineTxt = bufferedReader.readLine()) != null) {
                                if (lineTxt.matches("(.*\\*/\\s*$)")) { // 以*/结束
                                    break;
                                }
                            }
                        }
                        System.out.println("---------------" + lineTxt + "---------------");
                    } else if (lineTxt.matches("^#.*_H_?$")) { // 删掉头部的指针
                        continue;
                    } else if (lineTxt.matches("^#\\s*define.*") || lineTxt.matches("^#\\s*undef.*")) {
                        System.out.println("这里是#def：" + lineTxt);
                        if (lineTxt.endsWith("\\")) {
                            while ((lineTxt = bufferedReader.readLine()) != null) {
                                o31.write((lineTxt + Data.WinNewLine).getBytes());
                                if (!lineTxt.endsWith("\\")) {
                                    break;
                                }
                            }
                        } else {
                            o31.write((lineTxt + Data.WinNewLine).getBytes());
                        }
                        out.newLine();
                    } else if (lineTxt.matches("^#\\s*if.*")) {
                        int num = 1;
                        o31.write(lineTxt.getBytes());
                        o31.write(Data.WinNewLine.getBytes()); // win换行符写入
                        while ((lineTxt = bufferedReader.readLine()) != null) {
                            if (lineTxt.matches("^#\\s*if.*")) num++;
                            o31.write((lineTxt + Data.WinNewLine).getBytes());
                            if (lineTxt.matches("^#\\s*endif.*")) {
                                System.out.println();
                                num--;
                                if (num <= 0) break;
                            }
                        }
                    } else {
                        if (lineTxt.startsWith("#include")) {
                            String include_file = "";
                            if (lineTxt.contains("<")) {
                                include_file = "include/" + lineTxt.split("<|>")[1];
                            } else if (lineTxt.contains("\"")) {
                                include_file = "?/" + lineTxt.split("\"|\"")[1];
                            }
                            System.out.println(include_file);
                            out.write(include_file);
                            out.newLine();
                        } else if (lineTxt.matches("(^struct.*\\{$)|(^enum.*\\{$)|(^union.*\\{$)")) { // 对结构体或enum的处理
                            //int num = 1;
                            out.write(lineTxt.trim());
                            out.newLine();
                            System.out.println("==========" + lineTxt);

                            while ((lineTxt = bufferedReader.readLine()) != null) {
                                lineTxt = lineTxt.trim();
                                System.out.println("==========" + lineTxt);

                                if (lineTxt.startsWith("}")) {
                                    out.write(lineTxt.trim());
                                    out.newLine();
                                    break;
                                }
                                if (lineTxt.matches("^#\\s*if.*")) {
                                    int num = 1;
                                    o31.write(lineTxt.getBytes());
                                    o31.write(Data.WinNewLine.getBytes()); // win换行符写入
                                    while ((lineTxt = bufferedReader.readLine()) != null) {
                                        if (lineTxt.matches("^#\\s*if.*")) num++;
                                        o31.write((lineTxt + Data.WinNewLine).getBytes());
                                        if (lineTxt.matches("^#\\s*endif.*")) {
                                            System.out.println();
                                            num--;
                                            if (num <= 0) break;
                                        }
                                    }
                                } else if (lineTxt.startsWith("#")) {
                                    if (lineTxt.endsWith("\\")) {
                                        while ((lineTxt = bufferedReader.readLine()) != null) {
                                            o31.write((lineTxt + Data.WinNewLine).getBytes());
                                            if (!lineTxt.endsWith("\\")) {
                                                break;
                                            }
                                        }
                                    } else {
                                        o31.write((lineTxt + Data.WinNewLine).getBytes());
                                    }
                                } else { // 正常过滤
                                    lineTxt = lineTxt.replaceAll("/\\*{1,2}.*\\*/", ""); // 替换掉所有/**/注释
                                    if (lineTxt.matches("^.*\\s*/\\*.*") && !lineTxt.matches(".*\\*/\\s*$")) { // 以/*开头 却不以*/结尾。
                                        lineTxt = lineTxt.replaceAll("\\s*/\\*.*$", ""); // 替换掉所有/*注释，只有一边的，另一边换了行
                                        String tmp;
                                        while ((tmp = bufferedReader.readLine()) != null) {
                                            if (tmp.matches("(.*\\*/\\s*$)")) { // 以*/结束
                                                break;
                                            }
                                        }
                                    }
                                    System.out.println("0000000000000" + lineTxt);
                                    out.write(lineTxt.trim());
                                    if (lineTxt.matches(".*[;){]\\s*$")) { //换行的情况
                                        out.write(" ");
                                        out.newLine();
                                    }
                                    out.flush();//清除缓存向文件写入数据
                                }
                            }
                        } else if (lineTxt.startsWith("{")) {
                            while ((lineTxt = bufferedReader.readLine()) != null) {
                                if (lineTxt.startsWith("}")) {
                                    out.newLine(); //加一个空行好了
                                    //lineTxt = bufferedReader.readLine();
                                    break;
                                }
                            }
                        } else {
                            lineTxt = lineTxt.replaceAll("/\\*{1,2}[\\s\\S]*?\\*/", ""); // 替换掉所有/**/注释
                            if (lineTxt.matches("(^\\s*/{0,2}\\*.*)|(.*\\*/\\s*$)")) {
                                continue;
                            }
                            System.out.println(lineTxt);
                            out.write(lineTxt.trim());
                            out.write(" "); //所有的后面全都加空格
                            if (lineTxt.matches(".*[);{}]\\s*$")) { //换行的情况
                                out.newLine();
                            }
                            out.flush();//清除缓存向文件写入数据
                        }
                    }
                }
                out.close();
                o31.close();
                write.close();
                read.close();
            } else {
                System.out.println("找不到指定的文件");
                return;
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) {

        String path = "";
        //path = "D:\\java\\ideaProjects\\dynamic_validate\\src\\main\\java\\com\\example\\dynamic_validate\\util\\";
        //path = "G:\\研究生\\毕设\\我的毕设\\0小论文\\王梓资料\\数据\\phost\\pguestfs\\";
        path = "G:\\研究生\\毕设\\我的毕设\\系统实现\\checkSys数据\\8-最新数据\\手工提取数据\\linux\\";
        //path = "D:\\java\\doxygen\\project\\source\\test_module\\";
        String filePath1 = "", filePath2 = "", filePath3 = "";
        //filePath1 = path + "ring_file_publish.txt";
        //filePath1 = path + "communication_lru.txt";
        //filePath = path + "open.c";
        //filePath = path + "namei.c";
        filePath2 = path + "include\\linux\\fs.h";
        filePath3 = path + "include\\linux\\fs.h.txt";
        //filePath2 = path + "module.h";
        //filePath3 = path + "module.h.txt";


        List<String> list = readFuncInvoke(filePath1);
        //System.out.println(list);
        //System.out.println(list.size());
        readFuncDef(filePath2);
        distribLevelTxt(filePath3);


        //String dentryPath = "D:\\java\\ideaProjects\\dynamic_validate\\src\\main\\java\\com\\example\\dynamic_validate\\util";
        //
        //System.out.println(getFilesName(dentryPath)[0]);

    }

}

