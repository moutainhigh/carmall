package com.qunchuang.carmall.utils;

import com.aliyun.oss.OSSClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Curtain
 * @date 2019/1/23 9:40
 */
public class AliyunOSSUtil {

    // 目前只有"cn-hangzhou"这个region可用, 不要使用填写其他region的值
    public static final String REGION_CN_HANGZHOU = "cn-hangzhou";
    // 当前 STS API 版本
    public static final String STS_API_VERSION = "2015-04-01";
    // STS服务必须为 HTTPS
    public static final ProtocolType STS_PROTOCOL_TYPE = ProtocolType.HTTPS;

    static AssumeRoleResponse assumeRole(String accessKeyId, String accessKeySecret, String roleArn, String roleSessionName, String policy, long expired, ProtocolType protocolType)
            throws ClientException {
        // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
            IClientProfile profile = DefaultProfile.getProfile(REGION_CN_HANGZHOU,
                accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);

        // 创建一个 AssumeRoleRequest 并设置请求参数
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setVersion(STS_API_VERSION);
        request.setMethod(MethodType.POST);
        request.setProtocol(protocolType);
        request.setRoleArn(roleArn);
        request.setRoleSessionName(roleSessionName);
        request.setPolicy(policy);
        request.setDurationSeconds(expired);

        // 发起请求，并得到response
        return client.getAcsResponse(request);
    }


    public static AssumeRoleResponse getToken() {
        // 只有 子账号才能调用 AssumeRole接口
        // 阿里云主账号的AccessKeys不能用于发起AssumeRole请求
        // 请首先在RAM控制台创建子用户，并为这个用户创建AccessKeys
        String accessKeyId = "LTAIypfzxz9MJ7lt";
        String accessKeySecret = "GN7WfdI1hr1pYVdi1JvTSvsDNFPCRw";

        // AssumeRole API 请求参数: RoleArn, RoleSessionName, Policy, and
        // DurationSeconds
        // RoleArn可以到控制台上获取，路径是 访问控制 > 角色管理 > 角色名称 > 基本信息 > Arn
        String roleArn = "acs:ram::1939747795828478:role/ossandmessage";

        // RoleSessionName 是临时Token的会话名称，自己指定用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
        // 但是注意RoleSessionName的长度和规则，不要有空格，只能有'-' '_' 字母和数字等字符
        // 具体规则请参考API文档中的格式要求
        String roleSessionName = "alice-001";

        // 如何定制你的policy，如果policy为null，则STS的权限与roleArn的policy的定义的权限
        String policy = null;

        // 过期时间设置默认是一小时，单位秒有效值是[900, 3600]，即15分钟到60分钟。
        long expired = 3600;

        try {
            AssumeRoleResponse response = assumeRole(accessKeyId, accessKeySecret, roleArn, roleSessionName, policy, expired, STS_PROTOCOL_TYPE);
//            System.out.println("Expiration: " + response.getCredentials().getExpiration());
//            System.out.println("Access Key Id: " + response.getCredentials().getAccessKeyId());
//            System.out.println("Access Key Secret: " + response.getCredentials().getAccessKeySecret());
//            System.out.println("Security Token: " + response.getCredentials().getSecurityToken());
            return response;
        } catch (ClientException e) {
//            System.out.println("Error code: " + e.getErrCode());
//            System.out.println("Error message: " + e.getErrMsg());
            //可以做错误说明
            return null;
        }
    }

    /**
     * 后端图片直传测试
     *
     * @throws FileNotFoundException
     */
    public static void uploadImage(InputStream inputStream,String name) throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com/";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "LTAIypfzxz9MJ7lt";
        String accessKeySecret = "GN7WfdI1hr1pYVdi1JvTSvsDNFPCRw";
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        // 上传文件流。
//        InputStream inputStream = new FileInputStream("U:/images/11.jpg");
        ossClient.putObject("biya-image", name, inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
