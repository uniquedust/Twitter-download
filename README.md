# Twitter-download
下载twitter中的图片及视频
主要是参照该仓库进行开发的：https://github.com/caolvchong-top/twitter_download

不过他是用python写的，我能看懂，但是想在此基础上改动就有点困难了，python毕竟不是太熟，于是有了这个java版本的，也是用于练手
其中最恶心的应该就是分析返回的json了，可以在resource中看下，只是把最核心的json粘贴了一下，外面还有好多层。。。。。。
# 发布
* **2024/2/2**
    * 首次提交
        * 支持下载图片和视频，支持多用户下载，不同用户视频图片文件夹分开存放，支持自定义时间下载

* **2024/2/3**
    * 修复已经超过自定义日期仍反复请求的bug
    * 将文件的`修改日期`设置为推主的`发布日期`
* **2024/6/11**
    * 打印日志修改为log4j2方式
* **2024/6/13**
    * 修复文件名异常而无法下载问题（使用重试，不使用博主发布的文件名，改为系统生成uuid作为文件名）
* **2024/8/17**
    * 增量更新，根据已有文件获取到日期自动填充到日期左半侧
* ***2024/9/22*
    * 修复文件下载丢失问题 
      原因:有类似于" ".mp4和"    ".mp4这样的文件,导致之前的文件会被覆盖,接受gif文件,特殊json处理
# 注意事项基本都在application.properties中，写的应该算是比较详细了
```properties
#就算是windows路径也请用 '/' 而不是反斜杠(可以留空)
savePath=E:/test2

#填入要下载的用户名(@后面的字符)支持多用户下载用户名字间逗号(英文分号)隔开
screenName=
#填入 cookie (auth_token与ct0字段) //重要:替换掉其中的x即可 注意不要删掉分号
cookie=
#时间范围限制格式如 2024-01-01 11:42:30;2024-03-01 11:42:30 不填默认增量下载
timeRange=1990-01-01;2030-01-01

#是否需要下载图片,不填默认为true
isNeedPhoto=true
#是否需要下载视频
isNeedVideo=true

#动配置代理默认为空非必要无需填写 格式: ip:port
proxy=127.0.0.1:10809
#是否需要生成execl文档，一般应该都不用，练练手
isNeedEXecl=true
#请用英文分号分割
#execl表头，请注意需要遵循json返回的名称，否则无法赋值,仅获取了legacy和media的json中的标签，写其他的无用
#data->user->result->timeline_v2->timeline->instructions->分情况可能是moduleItems也可以能是entries->item->itemContent->tweet_results->result，然后到了legacy，media在legacy中
#简单整理了下几个，bookmark_count（收藏数量），created_at（创建时间），favorite_count（喜欢数量），full_text（文件标题），retweet_count（转发数量）,type(类型video/png)
#有个是固定的，url（视频或图片地址）,不可以删除，删除会对应不上
execlHead=url;type;created_at;full_text;


################################以下无需改动##############################
#获取用户信息的接口
UserByScreenName:https://twitter.com/i/api/graphql/xc8f1g7BYqr6VTzTbvNlGw/UserByScreenName?variables={"screen_name":"${screenName}","withSafetyModeUserFields":false}&features={"hidden_profile_likes_enabled":false,"hidden_profile_subscriptions_enabled":false,"responsive_web_graphql_exclude_directive_enabled":true,"verified_phone_label_enabled":false,"subscriptions_verification_info_verified_since_enabled":true,"highlights_tweets_tab_ui_enabled":true,"creator_subscriptions_tweet_preview_api_enabled":true,"responsive_web_graphql_skip_user_profile_image_extensions_enabled":false,"responsive_web_graphql_timeline_navigation_enabled":true}&fieldToggles={"withAuxiliaryUserLabels":false}
#获取用户媒体的接口，通过f12获取的api，但是新版的已经修改掉了此参数Le6KlbilFmSu-5VltFND-Q，导致不能成功获取数据了，现在这个还可以，不过不知道能维持多久
#count就是分页的数量/2，设置2爬取为1，设置为10爬取为5，7为3
#cursor-bottom-1753093524955332549 取的最后两个数字作为的页码
UserMediaTop:https://twitter.com/i/api/graphql/Le6KlbilFmSu-5VltFND-Q/UserMedia?variables={"userId":"${id}","count":300,
UserMediaBottom:"includePromotedContent":false,"withClientEventToken":false,"withBirdwatchNotes":false,"withVoice":true,"withV2Timeline":true}&features={"responsive_web_graphql_exclude_directive_enabled":true,"verified_phone_label_enabled":false,"creator_subscriptions_tweet_preview_api_enabled":true,"responsive_web_graphql_timeline_navigation_enabled":true,"responsive_web_graphql_skip_user_profile_image_extensions_enabled":false,"tweetypie_unmention_optimization_enabled":true,"responsive_web_edit_tweet_api_enabled":true,"graphql_is_translatable_rweb_tweet_is_translatable_enabled":true,"view_counts_everywhere_api_enabled":true,"longform_notetweets_consumption_enabled":true,"responsive_web_twitter_article_tweet_consumption_enabled":false,"tweet_awards_web_tipping_enabled":false,"freedom_of_speech_not_reach_fetch_enabled":true,"standardized_nudges_misinfo":true,"tweet_with_visibility_results_prefer_gql_limited_actions_policy_enabled":true,"longform_notetweets_rich_text_read_enabled":true,"longform_notetweets_inline_media_enabled":true,"responsive_web_media_download_video_enabled":false,"responsive_web_enhance_cards_enabled":false}
```
# 关于cookie的获取
就是找个请求，看下cookie就行
![](https://raw.githubusercontent.com/uniquedist/Twitter-download/main/src/main/resources/pic/cookie.png)
# 过程
![](https://raw.githubusercontent.com/uniquedist/Twitter-download/main/src/main/resources/pic/过程.png)
# 效果图
可以自行决定是否只下载图片或者只下载视频，视频图片文件名使用的是博主发布的标题名称
![](https://raw.githubusercontent.com/uniquedist/Twitter-download/main/src/main/resources/pic/效果图.png)
修改日期为博主发布的日期，默认有时候展示的是日期，而不是修改日期，需要自己调出来
![](https://raw.githubusercontent.com/uniquedist/Twitter-download/main/src/main/resources/pic/日期.png)
