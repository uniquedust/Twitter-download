package com.wgx.test;

import cn.hutool.core.util.URLUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * url加码解码测试
 * @author wgx
 * @date 2024/1/30 19:19
 */
public class UrlTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "https://twitter.com/i/api/graphql/2tLOJWwGuCTytDrGBg8VwQ/UserMedia?variables=%7B%22userId%22:%221070623326035460096%22,%22count%22:20,%22cursor%22:%22DAABCgABGFP869a___0KAAIYHfylihuwBQgAAwAAAAIAAA%22,%22includePromotedContent%22:false,%22withClientEventToken%22:false,%22withBirdwatchNotes%22:false,%22withVoice%22:true,%22withV2Timeline%22:true%7D&features=%7B%22responsive_web_graphql_exclude_directive_enabled%22:true,%22verified_phone_label_enabled%22:false,%22creator_subscriptions_tweet_preview_api_enabled%22:true,%22responsive_web_graphql_timeline_navigation_enabled%22:true,%22responsive_web_graphql_skip_user_profile_image_extensions_enabled%22:false,%22c9s_tweet_anatomy_moderator_badge_enabled%22:true,%22tweetypie_unmention_optimization_enabled%22:true,%22responsive_web_edit_tweet_api_enabled%22:true,%22graphql_is_translatable_rweb_tweet_is_translatable_enabled%22:true,%22view_counts_everywhere_api_enabled%22:true,%22longform_notetweets_consumption_enabled%22:true,%22responsive_web_twitter_article_tweet_consumption_enabled%22:true,%22tweet_awards_web_tipping_enabled%22:false,%22freedom_of_speech_not_reach_fetch_enabled%22:true,%22standardized_nudges_misinfo%22:true,%22tweet_with_visibility_results_prefer_gql_limited_actions_policy_enabled%22:true,%22rweb_video_timestamps_enabled%22:true,%22longform_notetweets_rich_text_read_enabled%22:true,%22longform_notetweets_inline_media_enabled%22:true,%22responsive_web_media_download_video_enabled%22:false,%22responsive_web_enhance_cards_enabled%22:false%7D";
        System.out.println(s);

        String decode = URLDecoder.decode(s, "utf-8");
        System.out.println(decode);

        String encode = URLEncoder.encode(s, "utf-8");
        System.out.println(encode);

        String encode1 = URLUtil.encode(decode);
        System.out.println(encode1);


    }

}
