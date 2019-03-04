package com.qunchuang.carmall.utils;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.device.TagAliasResult;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * 极光消息推送
 *
 * @author Curtain
 * @date 2018/8/17 8:56
 */
@Slf4j
public class JiGuangMessagePushUtil {

    private final static String MASTER_SECRET = "0a5a0e4e6b9511616bc7e2ba";
    private final static String APP_KEY = "581516d00a783ea0d5a3bb9d";
    public final static String CONTENT = "你有新的咨询单";
    public final static String TITLE = "毕亚";

    public static void sendMessage(String tag, String content) {

        JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());


        PushPayload payload = buildPushObject_ios_audienceMore_messageWithExtras(tag, content);

        try {
            PushResult result = jpushClient.sendPush(payload);
            //todo 极光推送失败情况      回导致订单回滚
            //处理方案一：保证订单 直接捕捉异常

            log.info("Got result - " + result);

//        } catch (APIConnectionException e) {
//            // Connection error, should retry later
//            log.error("Connection error, should retry later"+e);
//
//        } catch (APIRequestException e) {
//            // Should review the error, and fix the request
//            log.error("Should review the error, and fix the request"+e);
//            log.info("HTTP Status: " + e.getStatus());
//            log.info("Error Code: " + e.getErrorCode());
//            log.info("Error Message: " + e.getErrorMessage());

        } catch (Exception e) {
            //todo 为了不让特殊异常  影响订单进度  在此先捕获处理了

            //如果还是有问题 那么尝试更新jar包
            //https://community.jiguang.cn/search?q=Created%20instance%20with%20connectionTimeout%205%2C000%2C%20readTimeout%2030%2C000%2C%20maxRetryTimes%203%2C%20SSL%20Version%20TLS
            //https://github.com/jpush/jiguang-java-client-common/releases
            log.error("极光推送失败 原因：{}", e.getMessage());
        }
    }

    private static PushPayload buildPushObject_ios_audienceMore_messageWithExtras(String tag, String content) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
//                            .addAudienceTarget(AudienceTarget.registrationId("160a3797c85fd5a9f8f"))
                        .addAudienceTarget(AudienceTarget.alias(tag))
//                        .addAudienceTarget(AudienceTarget.tag(tag))
                        .build())
                .setNotification(Notification.android(content, TITLE, new HashMap<>()))
                .setMessage(Message.newBuilder()
                        .setMsgContent(content)
                        .addExtra("from", "JPush")
                        .build())
                .build();
    }

    public static void main(String[] args) throws APIConnectionException, APIRequestException {
//        sendMessage("WxjWEBAPGPueFsaW65dKP0A16",CONTENT);


        /*通过registrationId查询 与其绑定别名 标签 电话*/
        JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());

        TagAliasResult tagAlias = jpushClient.getDeviceTagAlias("1a0018970af85f7c5fa");
        System.out.println(tagAlias);

    }

}
