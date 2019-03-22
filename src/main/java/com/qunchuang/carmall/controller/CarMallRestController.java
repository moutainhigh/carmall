package com.qunchuang.carmall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.qunchuang.carmall.bean.WeChatMiniShareInfo;
import com.qunchuang.carmall.config.WeChatMiniResources;
import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.service.AdminService;
import com.qunchuang.carmall.service.VerificationService;
import com.qunchuang.carmall.utils.AliyunOSSUtil;
import com.qunchuang.carmall.utils.BosUtils;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Curtain
 * @date 2019/1/21 11:17
 */
@RestController
public class CarMallRestController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WeChatMiniResources weChatMiniResources;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/initAccount")
    public String account(String curtain) {
        return adminService.init(curtain);
    }

    @RequestMapping("/sts")
    public Object getStsToken() {
        AssumeRoleResponse resp = AliyunOSSUtil.getToken();
        Map<String, Object> result = new HashMap<>();
        result.put("bucketName", "biya-image");
        result.put("endpoint", "https://oss-cn-hangzhou.aliyuncs.com/");
        result.put("assumeRoleResponse", resp);
        result.put("resourceId", BosUtils.getZipUuid());
        return result;
    }

    @RequestMapping("/getCode")
    public String getCode(String phone) {
        return verificationService.getCode(phone);
    }

    @GetMapping(value = "/jsapisignature")
    @ResponseBody
    public Object createJsapiSignature(@RequestParam("url") String url) throws WxErrorException {
        //是否加上url域名判断
        return this.wxMpService.createJsapiSignature(url);
    }


    @RequestMapping("/getWxShareInfo")
    public WeChatMiniShareInfo getWxShareInfo(String key) {
        String value = (String) redisTemplate.opsForValue().get(key);
        JSONObject shareInfo = (JSONObject) JSON.parse(value);
        return JSONObject.toJavaObject(shareInfo, WeChatMiniShareInfo.class);
    }


    @RequestMapping("/getWxAuthentication")
    public String getWxaCode(String carModel) throws Exception {
        RestTemplate rest = new RestTemplate();
        Map<String, String> params = new HashMap<>(4);

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + weChatMiniResources.getAppId() + "&secret=" + weChatMiniResources.getSecret();

        String token = rest.getForObject(url, String.class);
        JSONObject parse = (JSONObject) JSON.parse(token);
        String value = (String) ((Map.Entry) parse.entrySet().toArray()[0]).getValue();

        String salesManId = Admin.getAdmin().getId();

        params.put("page", "pages/index/index");
        params.put("scene", salesManId);
        url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + value;


        WeChatMiniShareInfo shareInfo = new WeChatMiniShareInfo();
        shareInfo.setSalesManId(salesManId);

        if (!StringUtils.isEmpty(carModel)) {
            shareInfo.setCarModel(carModel);
            shareInfo.setFrom("carInfo");
        } else {
            shareInfo.setFrom("home");
        }
        String jsonString = JSONObject.toJSONString(shareInfo);
        redisTemplate.opsForValue().set(salesManId, jsonString);


        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        InputStream inputStream;
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        String body = JSON.toJSONString(params);
        StringEntity entity;
        entity = new StringEntity(body);
        entity.setContentType("image/png");

        httpPost.setEntity(entity);
        HttpResponse response;

        response = httpClient.execute(httpPost);
        inputStream = response.getEntity().getContent();


        String name = BosUtils.getZipUuid();
        String imgUrl = "https://image.buymycar.cn/" + name;
        AliyunOSSUtil.uploadImage(inputStream, name);

        adminService.generateWxCode(imgUrl);

        return imgUrl;


    }

}
