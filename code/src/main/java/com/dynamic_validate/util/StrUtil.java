package com.dynamic_validate.util;

import com.dynamic_validate.data.Data;
import com.sun.deploy.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StrUtil {

    /**
     * 可以借鉴一下名字：consumerToken()，我的“funcIgnoreNoStar”其实就是这个！
     *
     * 函数的IgnoreWord处理。
     *
     *特殊处理signed和unsigned：
     * 不能完全忽略，有时候是int的替换，所以先替换成int，
     * 如果除了int之外还有别的类型(char、short、int、long)，就删掉int，否则就是int，保留。
     */
    public static String funcIgnoreNoStar(String s) {
        String s1;
        do {
            s1 = s;
            //System.out.println("s1:" + s1);
            s = s.replaceAll("\\s+(" + Data.funcIgnoreWord + ")\\s+", " ");
            //特殊处理signed和unsigned
            s = s.replaceAll("\\b(signed|unsigned)\\b", "int");
            s = s.replaceAll("\\bint\\s+char\\b", "char");
            s = s.replaceAll("\\bint\\s+short\\b", "short");
            s = s.replaceAll("\\bint\\s+int\\b", "int"); // 没有单词边界，所以把intent也算成int了！
            s = s.replaceAll("\\bint\\s+long\\b", "long");
            // 下面这几个是alias
            s = s.replaceAll("\\blong\\s+int\\b", "long");
            s = s.replaceAll("\\bshort\\s+int\\b", "short");
            s = s.replaceAll("\\blong\\s+long\\b", "long");
            s = s.replaceAll("\\blong\\s+double\\b", "double");
            //System.out.println("s:" + s);
        } while (!s.equals(s1)); // 因为两边都有空格，所以连续的情况匹配不上，所以要循环。
        s = s.replaceAll("^(" + Data.funcIgnoreWord + ")\\s+", ""); //匹配开头情况

        //s = s.replaceAll("^\\s*" + Data.funcIgnoreWordBegin + "\\b\\s+", "");
        //s = s.replaceAll("\\s+\\b" + Data.funcIgnoreWord + "\\b\\s+", "");//必须赋值才能改变s的值！【单词边界\\b，否则报错】
        ////特殊处理signed和unsigned
        //s = s.replaceAll("\\s+signed|unsigned\\s+", "int");
        //s = s.replaceAll("\\s+int\\s+char\\s+", "char");
        //s = s.replaceAll("\\s+int\\s+short\\s+", "short");
        //s = s.replaceAll("\\s+int\\s+int\\s+", "int"); // 没有单词边界，所以把intent也算成int了！
        //s = s.replaceAll("\\s+int\\s+long\\s+", "long");

        return s;
    }

    public static String funcIgnoreWithStar(String s) {
        String s1;
        do {
            s1 = s;
            //System.out.println("s1:" + s1);
            s = s.replaceAll("\\s+(" + Data.funcIgnoreWord + ")\\s+", " ");
            //特殊处理signed和unsigned
            s = s.replaceAll("\\b(signed|unsigned)\\b", "int");
            s = s.replaceAll("\\bint\\s+char\\b", "char");
            s = s.replaceAll("\\bint\\s+short\\b", "short");
            s = s.replaceAll("\\bint\\s+int\\b", "int"); // 没有单词边界，所以把intent也算成int了！
            s = s.replaceAll("\\bint\\s+long\\b", "long");
            // 下面这几个是alias
            s = s.replaceAll("\\blong\\s+int\\b", "long");
            s = s.replaceAll("\\bshort\\s+int\\b", "short");
            s = s.replaceAll("\\blong\\s+long\\b", "long");
            s = s.replaceAll("\\blong\\s+double\\b", "double");
            //System.out.println("s:" + s);
        } while (!s.equals(s1)); // 因为两边都有空格，所以连续的情况匹配不上，所以要循环。
        s = s.replaceAll("^(" + Data.funcIgnoreWord + ")\\s+", ""); //匹配开头情况
        s = s.replaceAll("\\s*\\*\\s*", " "); // 把*及周围的空格替换成一个空格。

        System.out.println(s);
        return s;
    }

    public static String splitName(String p, int level) {
        String s_split = "";
        if (level == Data.FUNC_DECLARE || level == Data.FUNC) { // 文件中的函数声明，包括extern
            if (p.matches(Data.pointerReg)) {
                s_split = "\\(\\s*\\*\\s*\\b\\w+\\s*\\)\\s*\\("; // (*name)(
            } else {
                s_split = "\\b\\w+\\s*\\(";
            }
        } else if (level == Data.STRUCT) {
            s_split = "\\b\\w+\\s*\\{";
        } else if (level == Data.STRUCT_DECLARE) {
            s_split = "\\b\\w+\\s*;";
        } else {
            return "";
        }

        Matcher m = Pattern.compile(s_split).matcher(p);
        if (m.find()) {
            p = m.group().trim();
            p = p.substring(0, p.length() - 1).trim();
        } else {
            p = "no";
        }
        p = p.replaceAll("\\(|\\)|\\*", "").trim();//【如果有就替换，如果没有也不会出问题】

        return p;
    }

    /**
     * 用流实现：逗号隔开的字符串去重。
     */
    public static String distinct(String s) {
        if (!s.contains(",")) return s;
        //s = "fttt,yyy,uuu,uuu,ooo,ooo,";
        //将字符串变成数组，然后利用stream流变成集合
        List<String> throughLines = Arrays.stream(s.split(",")).collect(Collectors.toList());
        //利用stream流将集合去重
        List<String> throughLineList = throughLines.stream().distinct().collect(Collectors.toList());
        //然后再用Stringuitls.join将集合变成逗号分开的字符串
        s = StringUtils.join(throughLineList, ",");
        return s;
    }

    /**
     * 用流实现：逗号隔开的字符串去重。
     */
    public static int[] splitInt(String s) {
        if (s == null || s.equals("") || s.matches("(\\s+)|(,+)")) { // 由于listCheck用了findNotNull，实际上这种情况就不会有了！如果只有空白符，也考虑好了。
            return null;
        } else if (!s.contains(",")) { // 只有一个数字
            int[] m_t = {Integer.parseInt(s)};
            return m_t;
        } else {
            int[] member_type_id = Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
            return member_type_id;
        }
    }
}
