package com.wgx.test;

import org.apache.log4j.Logger;

/**
 * 日志测试,这个是1版本使用的和log4j.properties搭配
 * @author wgx
 * @date 2024/6/11
 */
public class Log4jOneTest {
    private static final Logger logger = Logger.getLogger(Log4jOneTest.class);

    public static void main(String[] args) {
        logger.trace("===TRACE===");
        logger.debug("===DEBUG===");
        logger.info("===INFO===");
        logger.warn("===WARN===");
        logger.error("===ERROR===");
        logger.fatal("===FATAL===");
    }
}
