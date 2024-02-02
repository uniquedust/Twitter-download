package com.wgx;

import lombok.Data;

/**
 * 用户信息类
 *
 * @author wgx
 * @date 2024/1/30 19:19
 */
@Data
public class UserInfo {
    //用户id( @后面的 )
    private String screenName;
    //cookie
    private String cookie;
    //保存路径
    private String savePath;
    //时间范围
    private String timeRange;
    //代理
    private String proxy;
    //rest_id用户id
    private String restId;

    //接口
    private String userByScreenName;
    private String userMediaTop;
    private String userMediaBottom;

    private Boolean isNeedEXecl;
    private Boolean isNeedPhoto;
    private Boolean isNeedVideo;
    private String execlHead;
}
