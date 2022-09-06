package com.example.dynamic_validate.util;

import java.util.List;

public class GetFunc implements DealLine {
    /**
     * 一行行读取文件，如果下一行是左大括号“{”，
     * 获取本行及上一行空行之间的数据，并且去掉换行符，合成一行。
     *
     * 例子：
     *    int finish_open(struct file *file, struct dentry *dentry,
     *    int (*open)(struct inode *, struct file *),
     *    int *opened)
     *    {xxx}
     */
    @Override
    public List<String> deal(String line, List<String> list, String... args) {
        return null;
    }
}
