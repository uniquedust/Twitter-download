package com.wgx;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wgx.utils.DataUtil;
import com.wgx.utils.ThreadPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 运行类
 *
 * @author wgx
 * @date 2024/1/30 19:19
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);


    private static final String USERAGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";
    private static final String AUTHORIZATION = "Bearer AAAAAAAAAAAAAAAAAAAAANRILgAAAAAAnNwIzUejRCOuH5E6I8xnZz4puTs%3D1Zv7ttfk8LF81IUq16cHjhLTvJu4FA33AGWWjCpTnA";
    private static final String REFERER = "https://twitter.com/";
    private static final String CURSOR = "\"cursor\":\"${cursor}\",";

    //存放分页标志
    private static final Map<String, String> pageMap;
    //页码资源标记
    private static final String PAGEFLAG = "bottom";
    //资源标记
    private static final String RESOURCEFLAG = "profile";
    //初始日期
    private static final String INITDATE = "1970-01-01";


    //根据用户名映射的视频图片url列表
    private static final Map<String, List<String>> resourceMap;


    private static final UserInfo info;

    static {
        info = DataUtil.getProperty();
        pageMap = new HashMap<>();
        resourceMap = new HashMap<>();
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        //发起UserByScreenName请求获取rest_id及excel文件名
        Map<String, Object> user = getUserByScreenName();

        //发起UserMedia下载资源
        getUserMedia(user, null);
        //需要关闭线程否则程序不会停止
        ThreadPool.shutdown();
        while (true) {// 等待所有任务都执行结束
            if (ThreadPool.getActiveCount() <= 0) {
                logger.info("====================总耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒=================");
                break;
            }
        }
    }

    /**
     * 发起UserMedia下载资源
     *
     * @param map  存储restid，及文件位置及execl位置
     * @param list 用于递归，第一次用于存储map.get("list")
     */
    public static void getUserMedia(Map<String, Object> map, List<String> list) {
        //递归结束条件
        if (list != null && list.size() == 0) {
            return;
        }

        List<String> restIdList = list == null ? (List<String>) map.get("list") : list;
        if (CollUtil.isEmpty(restIdList)) {
            logger.error("UserByScreenName请求出问题了！");
            return;
        }

        //获取请求头
        Map<String, String> headMap = getHeaderMap(info.getCookie());
        Map<String, Future> futureMap = new HashMap<>();


        String needDelete = "";
        for (String id : restIdList) {
            //说明到最后一页了
            if (map.containsKey("flag" + id) && Boolean.parseBoolean(map.get("flag" + id).toString())) {
                needDelete = id;
            }

            String name = map.get(id).toString();
            //开启了execl，id=>execl文件位置，未开启，id=>property中的设置
            if (info.getIsNeedEXecl()) {
                name = name.substring(name.lastIndexOf(File.separator) + 1, name.lastIndexOf("."));
            }
            String url = info.getUserMediaTop().replace("${id}", id);
            String bottom = info.getUserMediaBottom();
            if (pageMap.containsKey(id)) {
                url = url + CURSOR.replace("${cursor}", pageMap.get(id).toString()) + bottom;
            } else {
                url += bottom;
            }
            //发起请求
            headMap.put(Header.REFERER.getValue(), REFERER + name);
            //不需要对url加码
//            String[] split = url.split("\\?");
//            url = split[0]+"?"+URLUtil.encode(split[1]);
            logger.info("资源url:" + url);
            futureMap.put(id, getFutureMap(url, headMap));
        }

        if (StrUtil.isNotBlank(needDelete)) {
            restIdList.remove(needDelete);
        }

        //处理请求回来的json
        for (String id : restIdList) {
            Date maxModifyDate = (Date) map.get(id + "maxdate");

            Future future = futureMap.get(id);
            String res = null;
            try {
                res = future.get().toString();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            JSONObject data = JSONObject.parseObject(res);
            Map<String, String> videoMap = new HashMap<>();
            Map<String, String> photoMap = new HashMap<>();
            //日期的相关信息应该存在videoMap，photoMap，但是改起来费劲，就新加个参数
            Map<String, Date> dateMap = new HashMap<>();
            if (data != null && data.containsKey("data") && data.getJSONObject("data").getJSONObject("user") != null) {
                JSONArray instructions = data.getJSONObject("data").getJSONObject("user").getJSONObject("result").getJSONObject("timeline_v2").getJSONObject("timeline").getJSONArray("instructions");
                CsvWriter writer = null;
                if (info.getIsNeedEXecl()) {
                    writer = CsvUtil.getWriter(map.get(id).toString(), CharsetUtil.CHARSET_GBK, true);
                }

                //用于判断数据是否已经没有了，当没有数据时候，仅有TimelineAddEntries，并且entryId没有包含”profile“字符
                boolean flag = true;
                //这里就不作异常处理了，如果报错估计就是接口返回参数变了
                //分两种情况，一是第一页，二是其他页（第一页的返回参数和其他页不同）
                for (int i = 0; i < instructions.size(); i++) {
                    JSONObject ins = instructions.getJSONObject(i);
                    String type = ins.getString("type");

                    //其他页
                    if ("TimelineAddToModule".equals(type)) {
                        JSONArray videoPhotoArray = ins.getJSONArray("moduleItems");
                        //将最大修改时间到dateMap中
                        dateMap.put("maxModifyDate",maxModifyDate);
                        //从保存视频图片的json中提取有用的信息保存到map中
                        flag = dealItemJson(videoPhotoArray, videoMap, photoMap, writer, dateMap);
                    }

                    //这个里面可以获取到页码标志，并且可以获取到第一页的数据
                    if ("TimelineAddEntries".equals(type)) {
                        JSONArray entries = ins.getJSONArray("entries");
                        for (int j = 0; j < entries.size(); j++) {
                            String entryId = entries.getJSONObject(j).getString("entryId");
                            if (entryId.contains(PAGEFLAG)) {
                                String value = entries.getJSONObject(j).getJSONObject("content").getString("value");
                                //设置一个旧值，作为判断数据是否到底,即使到底了值也在不断变化，用的也是分页
                                //pageMap.put("old" + id, pageMap.getOrDefault(id, ""));
                                pageMap.put(id, value);
                            }

                            if (entryId.contains(RESOURCEFLAG)) {
                                JSONArray videoPhotoArray = entries.getJSONObject(j).getJSONObject("content").getJSONArray("items");
                                //将最大修改时间到dateMap中
                                dateMap.put("maxModifyDate",maxModifyDate);
                                //从保存视频图片的json中提取有用的信息保存到map中
                                flag = dealItemJson(videoPhotoArray, videoMap, photoMap, writer, dateMap);
                            }

                        }
                    }


                }
                //判断是否到了最后一页或者推文日期已经小于设置的最小日期，用于移除restIdList中的用户
                map.put("flag" + id, flag);

                if (writer != null) {
                    writer.close();
                }


            } else {
                logger.error("-----------------请求出错了,需要检查参数及接口-----------");
            }

            //将获取到的图片视频url保存到本地
            String path = map.get(id + "file").toString();
            SaveUrlFile(path, videoMap, photoMap, dateMap);
        }
        getUserMedia(map, restIdList);

    }

    /**
     * 从保存视频图片的json中提取有用的信息保存到map中
     *
     * @param videoPhotoArray 保存视频图片的json
     * @param videoMap        文件名->url
     * @param photoMap        文件名->url
     * @param writer          csvwriter
     * @param dateMap         日期的相关信息应该存在videoMap，photoMap，但是改起来费劲，就新加个参数
     */
    public static boolean dealItemJson(JSONArray videoPhotoArray, Map<String, String> videoMap, Map<String, String> photoMap, CsvWriter writer, Map<String, Date> dateMap) {
        logger.info("------------此次请求视频图片总数量（未过滤）：" + videoPhotoArray.size() + "----------");
        Date maxModifyDate = dateMap.get("maxModifyDate");
        //用于判断推文日期是否已经小于设置的最小日期，当小于的时候，递归也没有必要再进行下去
        boolean flag = false;
        for (int k = 0; k < videoPhotoArray.size(); k++) {
            JSONObject results = videoPhotoArray.getJSONObject(k).getJSONObject("item").getJSONObject("itemContent").getJSONObject("tweet_results").getJSONObject("result");
            JSONObject legacy = results.getJSONObject("legacy");
            //获取创建日期
            Date create = DateUtil.parse(legacy.getString("created_at"));
            //判断推文日期是否小于设置的最小日期
            flag = DataUtil.checkMinDate(create, info.getTimeRange(),maxModifyDate);

            //判断该推文是否符合日期要求
            if (!DataUtil.compareDate(create, info.getTimeRange(),maxModifyDate)) {
                continue;
            }

            legacy.put("created_at", create);
            //从entries中获取图片和video的url地址
            String[] firstVideoPhotos = getVideoPhotos(legacy, videoMap, photoMap, dateMap);
            //将内容写到execl中
            if (firstVideoPhotos != null) {
                writer.write(
                        firstVideoPhotos
                );
            }

        }
        return flag;
    }

    /**
     * 从第legacy中获取图片和video的url地址,并返回需要的excel内容
     *
     * @param legacy  legacy的json
     * @param video   文件名->url
     * @param photo   文件名->url
     * @param dateMap 日期的相关信息应该存在videoMap，photoMap，但是改起来费劲，就新加个参数(用来存推文发布时间的,然后将文件的修改时间改为发布时间)
     * @return 需要的excel内容
     */
    public static String[] getVideoPhotos(JSONObject legacy, Map<String, String> video, Map<String, String> photo, Map<String, Date> dateMap) {
        //legacy中有entities和extended_entities，目前看暂时是一样的，取用entities中的值
        JSONObject media = legacy.getJSONObject("entities").getJSONArray("media").getJSONObject(0);
        //获取下文件的标题并处理下标题格式
        String title = DataUtil.dealString(legacy.getString("full_text"));
        String[] split = info.getExeclHead().split(";");
        String[] content = new String[split.length];
        Date create = DateUtil.parse(legacy.getString("created_at"));
        dateMap.put(title, create);

        String type = media.getString("type");
        if (info.getIsNeedPhoto() && "photo".equals(type)) {
            String url = media.getString("media_url_https");
            content[0] = url;
            photo.put(title, url);
        }
        if (info.getIsNeedVideo() && "video".equals(type)) {
            JSONArray variants = media.getJSONObject("video_info").getJSONArray("variants");
            //找比特率最大的视频取url
            JSONObject tmp = new JSONObject();
            int max = 0;
            for (int i = 0; i < variants.size(); i++) {
                JSONObject v = variants.getJSONObject(i);
                int bitrate = v.getIntValue("bitrate");
                if (bitrate > max) {
                    max = bitrate;
                    tmp = v;
                }
            }
            content[0] = tmp.getString("url");
            video.put(title, tmp.getString("url"));
        }


        //判断是否需要将值打印到execl中
        if (info.getIsNeedEXecl() && ((info.getIsNeedPhoto() && "photo".equals(type)) || (info.getIsNeedVideo() && "video".equals(type)))) {

            boolean flag = false;
            //content[0]用来保存url
            for (int i = 1; i < split.length; i++) {
                if ("full_text".equals(split[i])) {
                    content[i] = title;
                    continue;
                }
                if (legacy.containsKey(split[i])) {
                    flag = true;
                    content[i] = legacy.getString(split[i]);
                }
                if (media.containsKey(split[i])) {
                    flag = true;
                    content[i] = media.getString(split[i]);
                }
            }
            if (flag) {
                return content;
            }
        }
        return null;
    }


    /**
     * 保存文件
     *
     * @param path    文件路径
     * @param video   文件名->url
     * @param photo   文件名->url
     * @param dateMap 日期的相关信息应该存在videoMap，photoMap，但是改起来费劲，就新加个参数(用来存推文发布时间的,然后将文件的修改时间改为发布时间)
     */
    public static void SaveUrlFile(String path, Map<String, String> video, Map<String, String> photo, Map<String, Date> dateMap) {
        if (info.getIsNeedVideo()) {
            logger.info("----实际下载视频数量：" + video.size() + "----");
        }
        if (info.getIsNeedPhoto()) {
            logger.info("----实际下载图片数量：" + photo.size() + "----");
        }

        if (info.getIsNeedVideo()) {
            String videoPath = path + File.separator + "v";
            new File(videoPath).mkdir();
            dealVideoAndPhoto(video, videoPath, true, dateMap);
        }
        if (info.getIsNeedPhoto()) {
            String photoPath = path + File.separator + "p";
            new File(photoPath).mkdir();
            dealVideoAndPhoto(photo, photoPath, false, dateMap);
        }
    }

    /**
     * 保存视频文件到本地
     *
     * @param videoPhoto video或photo的map，文件名->url
     * @param path       视频或者图片的地址
     * @param isVideo    是否为视频
     * @param dateMap    日期的相关信息应该存在videoMap，photoMap，但是改起来费劲，就新加个参数(用来存推文发布时间的,然后将文件的修改时间改为发布时间)
     */
    public static void dealVideoAndPhoto(Map<String, String> videoPhoto, String path, boolean isVideo, Map<String, Date> dateMap) {
        //使用多线程保存文件
        ThreadPoolExecutor threadPool = ThreadPool.getThreadPool();
        //获取请求头
        Map<String, String> headMap = getHeaderMap(null);
        for (Map.Entry<String, String> key : videoPhoto.entrySet()) {
            String url = key.getValue();
            String fileName = key.getKey();
            threadPool.submit(() -> {
                HttpRequest request = HttpRequest.get(url).addHeaders(headMap);
                String proxy = info.getProxy();
                if (StrUtil.isNotBlank(proxy)) {
                    String[] split = proxy.split(":");
                    request.setHttpProxy(split[0], Integer.parseInt(split[1]));
                }
                InputStream body = request.execute().bodyStream();
                //使用hutool
                try {
                    String tmp = "";
                    if (isVideo) {
                        tmp = path + File.separator + fileName + ".mp4";

                    } else {
                        tmp = path + File.separator + fileName + ".png";
                    }
                    //文件不存在进行写入
                    if (!FileUtil.exist(tmp)) {
                        File file = new File(tmp);
                        FileUtil.writeFromStream(body, file);
                        file.setLastModified(dateMap.get(fileName).getTime());
                    }
                } catch (Exception e) {
                    //失败直接使用对应的英文字母作为文件名
                    String tempFilename = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
                    //直接重试
                    String tmp = "";
                    if (isVideo) {
                        tmp = path + File.separator + tempFilename + ".mp4";

                    } else {
                        tmp = path + File.separator + tempFilename + ".png";
                    }
                    try {
                        if (!FileUtil.exist(tmp)) {
                            File file = new File(tmp);
                            FileUtil.writeFromStream(body, file);
                            file.setLastModified(dateMap.get(fileName).getTime());
                        }
                    } catch (Exception x) {
                        //再次失败打印错误日志
                        logger.error("文件创建失败：地址为" + url + ";文件名：" + fileName);
                        logger.error(x.getMessage(), x);
                    }

                }
            });
        }
    }


    /**
     * 发起UserByScreenName请求获取rest_id及excel文件名
     */
    public static Map<String, Object> getUserByScreenName() {
        Map<String, Object> map = new HashMap<>();
        List<String> restIdList = new ArrayList<>(5);
        String screenName = info.getScreenName();
        if (StrUtil.isBlank(screenName)) {
            logger.error("请设置id名字");
            return map;
        }
        String cookie = info.getCookie();
        if (StrUtil.isBlank(cookie)) {
            logger.error("请设置cookie");
            return map;
        }
        //使用多线程来获取用户的id及打印
        String[] nameArray = screenName.split(";");
        //获取请求头
        Map<String, String> headMap = getHeaderMap(cookie);
        Map<String, Future> futureMap = new HashMap<>();
        for (String name : nameArray) {
            String url = info.getUserByScreenName().replace("${screenName}", name);
            logger.info(url);

            headMap.put(Header.REFERER.getValue(), REFERER + name);

            //将返回值存到futuremap中
            futureMap.put(name, getFutureMap(url, headMap));
        }

        //获取多线程的返回结果
        for (String name : nameArray) {
            Future future = futureMap.get(name);
            String body = null;
            try {
                body = future.get().toString();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            JSONObject data = JSONObject.parseObject(body);
            //解析返回的json
            if (data != null && data.containsKey("data") && data.getJSONObject("data").containsKey("user")) {
                JSONObject result = data.getJSONObject("data").getJSONObject("user").getJSONObject("result");
                restIdList.add(result.getString("rest_id"));

                JSONObject legacy = result.getJSONObject("legacy");
                //输出下用户基本信息
                logger.info("<======基本信息=====>");
                logger.info("名称:" + legacy.getString("name"));
                logger.info("昵称:" + name);
                logger.info("含图片/视频/音频推数(不含转推):" + legacy.getString("media_count"));
                logger.info("<=======开始爬取===========>");

                //创建对应的文件夹
                String path = info.getSavePath() + File.separator + name;
                File file = new File(path);
                file.mkdirs();
                //保存下文件夹地址
                map.put(result.getString("rest_id") + "file", path);

                //如果对应目录下已经有文件了,则遍历取下文件中最大的修改时间
                DateTime maxModifyTime = DateUtil.parse(INITDATE, "yyyy-MM-dd");
                getAllFiles(file, maxModifyTime);
                map.put(result.getString("rest_id") + "maxdate", maxModifyTime);

                map.put(result.getString("rest_id"), name);
                if (info.getIsNeedEXecl()) {
                    String fileName = path + File.separator + name + ".csv";
                    //直接创建csv文件，比弄execl文件方便点，但是不能调整列宽
                    //指定路径和编码,window默认编码为gbk，所以设置为gbk，而不是utf-8,这里需要覆盖
                    CsvWriter writer = CsvUtil.getWriter(fileName, CharsetUtil.CHARSET_GBK);
                    //按行写出
                    writer.write(
                            new String[]{"名称:" + legacy.getString("name"), "昵称：" + name, "时间范围：" + info.getTimeRange(), "存储路径：" + fileName},
                            new String[]{},
                            info.getExeclHead().split(";")
                    );
                    //关闭io流
                    writer.close();
                    //保存下文档位置
                    map.put(result.getString("rest_id"), fileName);
                }


            } else {
                logger.error("请求错误，请检查用户" + name + "是否存在！");
                return map;
            }
        }
        map.put("list", restIdList);
        return map;
    }

    /**
     * 递归遍历此路径的文件夹,找到其中的最大修改时间
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

    /**
     * 将请求的多线程提出来(get请求)
     *
     * @param url    http的url
     * @param header http的header
     */
    public static Future<Object> getFutureMap(String url, Map<String, String> header) {
        ThreadPoolExecutor threadPool = ThreadPool.getThreadPool();
        return threadPool.submit(() -> {
            HttpRequest request = HttpRequest.get(url).addHeaders(header);
            String proxy = info.getProxy();
            if (StrUtil.isNotBlank(proxy)) {
                String[] split = proxy.split(":");
                request.setHttpProxy(split[0], Integer.parseInt(split[1]));
            }
            return request.execute().body();
        });
    }

    /**
     * 获取http请求头
     */
    public static Map<String, String> getHeaderMap(String cookie) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put(Header.USER_AGENT.getValue(), USERAGENT);
        if (StrUtil.isNotBlank(cookie)) {
            //.cookie(cookie)也行
            headMap.put(Header.COOKIE.getValue(), cookie);
            //X-Csrf-Token就是cookie的ct0部分
            headMap.put("X-Csrf-Token", cookie.substring(cookie.lastIndexOf("=") + 1));
        }
        headMap.put(Header.AUTHORIZATION.getValue(), AUTHORIZATION);
        return headMap;
    }
}
