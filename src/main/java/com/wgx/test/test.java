package com.wgx.test;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;


/**
 * @author wgx
 * @date 2024/1/30 19:19
 */
public class test {
    public static final String PATTERN = "(\\d\\d)(\\d\\d)";
    public static void main(String[] args) throws Exception {
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
//        Properties properties = new Properties();
//        properties.load(DataUtil.class.getClassLoader().getResourceAsStream("application.properties"));
//        String property = properties.getProperty("isNeedVideo", "true");
//        System.out.println(property);


//        DateTime parse = DateUtil.parse("2021-01-01");
//        for(int i=0;i<10;i++){
//            File file = new File("e:/11/"+i + ".mp4");
//            FileUtil.touch(file);
//            file.setLastModified(parse.getTime());
//        }


        // 指定视频文件路径
        String videoFilePath = "E:\\test2\\herla172\\1.mp4";  // 替换为您的视频文件路径

        // 指定新的媒体创建日期（这里假设您要将媒体创建日期修改为当前时间）
        String newCreationDate = "20240103120000";  // 格式为yyyyMMddHHmmss

        // 构建FFmpeg命令
         String ffmpegCommand = "d:/ffmpeg -i " + videoFilePath + " -metadata creation_time=" + newCreationDate + " -c copy -y " + videoFilePath;

        // 执行FFmpeg命令
        Process process = Runtime.getRuntime().exec(ffmpegCommand);

        // 等待进程执行结束
        process.waitFor();

        // 打印信息
        System.out.println("媒体创建日期已修改为当前时间。");

    }



}
