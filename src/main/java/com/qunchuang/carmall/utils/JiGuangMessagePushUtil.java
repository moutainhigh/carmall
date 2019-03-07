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

    private final static String MASTER_SECRET = "76c2e9f29a8199ddc77e946e";
    private final static String APP_KEY = "3196152f8e7226ad72259ee3";
    public final static String CONTENT = "你有新的咨询单";
    public final static String TITLE = "毕亚";

    public static void sendMessage(String tag, String content) {

        JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());

        PushPayload payload = buildPushObject_ios_audienceMore_messageWithExtras(tag, content);

        try {
            PushResult result = jpushClient.sendPush(payload);
            log.info("Got result - " + result);

        } catch (Exception e) {
            log.error("极光推送失败 原因：{}", e.getMessage());
        }
    }

    private static PushPayload buildPushObject_ios_audienceMore_messageWithExtras(String tag, String content) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
//                            .addAudienceTarget(AudienceTarget.registrationId("160a3797c85fd5a9f8f"))
//                        .addAudienceTarget(AudienceTarget.alias(tag))
                        .addAudienceTarget(AudienceTarget.tag(tag))
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

        TagAliasResult tagAlias = jpushClient.getDeviceTagAlias("1104a89792f75418aec");

        System.out.println(jpushClient.getTagList());
        System.out.println(tagAlias);

        sendMessage(tagAlias.tags.get(0),CONTENT);

    }

}
