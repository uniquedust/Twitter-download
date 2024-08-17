package com.wgx.test;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.io.File;
import java.util.Date;

/**
 * 测试下获取最大修改时间
 * @author wgx
 * @date 2024/8/17
 */
public class FileModifyDateTest {
    public static void main(String[] args) {
        File file = new File("D:\\Download\\test");
        DateTime parse = DateUtil.parse("1970-01-01", "yyyy-MM-dd");
        getAllFiles(file, parse);
        System.out.println(parse);

    }


    /**
     * 遍历此路径的文件夹,找到其中的最大修改时间
     */
    private static void getAllFiles(File file, Date maxDate) {
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        //递归
                        getAllFiles(file2, maxDate);
                    } else {
                        long l = file2.lastModified();
                        if (l > maxDate.getTime()) {
                            maxDate.setTime(l);
                        }
                    }
                }
            }
        }
    }
}
