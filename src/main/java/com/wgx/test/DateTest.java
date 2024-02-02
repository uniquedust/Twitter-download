package com.wgx.test;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * 测试以下日期的计算
 *
 * @author wgx
 * @date 2024/1/30 19:19
 */
public class DateTest {
    public static void main(String[] args) {
        Date now = new Date();
        System.out.println(now);
        String s = "Mon Jan 01 11:42:30 +0000 2024";
        //默认转换成了该形式：2024-01-01 11:42:30
        Date parse = DateUtil.parse(s);
        System.out.println(parse);
        //如果date1 < date2，返回数小于0，date1==date2返回0，date1 > date2 大于0,date1在前面
        int compare = DateUtil.compare(now, parse);
        System.out.println(compare);
    }
}
