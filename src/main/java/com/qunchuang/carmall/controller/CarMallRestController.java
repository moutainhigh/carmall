package com.qunchuang.carmall.controller;

import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.qunchuang.carmall.service.AdminService;
import com.qunchuang.carmall.service.VerificationService;
import com.qunchuang.carmall.utils.AliyunOSSUtil;
import com.qunchuang.carmall.utils.BosUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private SessionRepository sessionRepository;

    @RequestMapping("/initAccount")
    public String account(String curtain){
        return adminService.init(curtain);
    }

    @RequestMapping("/sts")
    public Object getStsToken() {
        AssumeRoleResponse resp= AliyunOSSUtil.getToken();
        Map<String,Object> result=new HashMap<>();
        result.put("bucketName","biya-image");
        result.put("endpoint","http://oss-cn-hangzhou.aliyuncs.com/");
        result.put("assumeRoleResponse",resp);
        result.put("resourceId", BosUtils.getZipUuid());
        return result;
    }

    @RequestMapping("/getCode")
    public String getCode(String phone){
        return verificationService.getCode(phone);
    }

    @RequestMapping("/xxx")
    public void getSession(){
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
