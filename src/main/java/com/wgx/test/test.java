package com.wgx.test;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.wgx.utils.DataUtil;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author wgx
 * @date 2024/1/30 19:19
 */
public class test {
    public static final String PATTERN = "(\\d\\d)(\\d\\d)";
    public static void main(String[] args) throws IOException {
//        String  target = "profile-grid-0";
//        System.out.println(target.contains("profile "));
//
//
//        String ss = " Wed Feb 24 04:24:17 +0000 2021";
//        Date date = new Date(ss);
//        System.out.println(date);
//        String format = DateUtil.format(date,"yyyy");
//        System.out.println(format);
//
//        FileUtil.touch("e:/test3/test.mp4");

//        String name ="1234ffffffffffff222";
//        Pattern r = Pattern.compile(PATTERN);
//        Matcher m = r.matcher(name);
//        if (m.find()) {
//            System.out.println(m.group(0));
//            System.out.println(m.group(1));
//            System.out.println(m.group(2));
//            System.out.println(m.group(3));
//            System.out.println(m.group(4));
//        }

//        int i = 0;
//        while(i<10){
//            if(i++ ==5){
//                return;
//            }
//            System.out.println(i);
//        }
//
//        String result2 = "3333333@借记卡酷酷".replaceAll("@.*", "");
//        System.out.println(result2);
//        String input = "这是一个   带有  多 个 空格 和 \n换行符 的 字符串";
//        String output = input.replaceAll(" +|\\n", "");
//        System.out.println(output); // 输出: "这是一个带有多个空格和换行符的字符串"
//
//        FileUtil.touch("E:/test2\\cc731216\\v\\消失了一段时间现在回来了.mp4");

//        List<String> list = new ArrayList<>();
//        list.add("s");
//        list.remove("s");
//        System.out.println(list.size());

//        CsvWriter writer = CsvUtil.getWriter("e:/test.csv", CharsetUtil.CHARSET_GBK,true).setAlwaysDelimitText(true).setLineDelimiter(";".toCharArray());
//        //按行写出
//        writer.write(
//                new String[]{"昵称：", "时间ddddddddddddddddddddddddddd范围：", "存储路径："}
//        );
//        writer.flush();
//        //关闭io流
//        writer.close();
//
//        writer = CsvUtil.getWriter("e:/test.csv", CharsetUtil.CHARSET_GBK,true).setAlwaysDelimitText(true);
//        //按行写出
//        writer.write(
//                new String[]{"昵称22222：", "时间ssddddddddddddddddddddddddddddddddddddss范围：", "存储路径："}
//                );
//        writer.flush();
//        //关闭io流
//        writer.close();
        Properties properties = new Properties();
        properties.load(DataUtil.class.getClassLoader().getResourceAsStream("application.properties"));
        String property = properties.getProperty("isNeedVideo", "true");
        System.out.println(property);
    }


}
