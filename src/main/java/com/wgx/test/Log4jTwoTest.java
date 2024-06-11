package com.wgx.test;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日志测试,这个是2版本使用的和log4j2.xml搭配
 * @author wgx
 * @date 2024/6/11
 * https://www.cnblogs.com/antLaddie/p/15904895.html
 */
public class Log4jTwoTest {
    private static final Logger logger = LogManager.getLogger(Log4jTwoTest.class);


    public static void main(String[] args) {
        logger.trace("===TRACE===");
        logger.debug("===DEBUG===");
        logger.info("===INFO===");
        logger.warn("===WARN===");
        logger.error("===ERROR===");
        logger.fatal("===FATAL===");
    }
}
