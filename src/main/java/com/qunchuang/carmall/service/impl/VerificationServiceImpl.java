package com.qunchuang.carmall.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.service.VerificationService;
import com.qunchuang.carmall.utils.AliyunMessageUtil;
import com.qunchuang.carmall.utils.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Curtain
 * @date 2019/1/23 15:56
 */
@Service
@Slf4j
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String VERIFICATION = "VERIFICATION";

    @Override
    public String getCode(String phone) {
        String randomNum = NumberUtil.createRandomNum(6);
        String jsonContent = "{\"code\":\"" + randomNum + "\"}";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phoneNumber", phone);
        paramMap.put("jsonContent", jsonContent);
        paramMap.put("templateCode", "SMS_156895894");
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = AliyunMessageUtil.sendSms(paramMap);
        } catch (ClientException e) {
            if (!(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))) {
                log.error("获取验证码失败，phone = %s ,cause = %s", phone, e.getMessage());
                throw new CarMallException(CarMallExceptionEnum.GET_VERIFICATION_CODE_FAIL);
            }
        }

        redisTemplate.opsForValue().set(VERIFICATION + phone, randomNum);

        return randomNum;
    }

    @Override
    public void verify(String phone, String code) {
        String key = VERIFICATION + phone;
        String rs = String.valueOf(redisTemplate.opsForValue().get(key));
        if (!rs.equals(code)) {
            log.error("验证码不正确，phone = %s,code = %s,rsCode = %s", phone, code, rs);
            throw new CarMallException(CarMallExceptionEnum.VERIFY_CODE_FAIL);
        }
    }
}
