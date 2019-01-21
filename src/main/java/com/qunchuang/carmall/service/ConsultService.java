package com.qunchuang.carmall.service;

import ch.qos.logback.core.ConsoleAppender;
import com.qunchuang.carmall.domain.Consult;

/**
 * @author Curtain
 * @date 2019/1/21 8:18
 */
public interface ConsultService {

    /**
     * 创建咨询单
     * @param consult
     * @return
     */
    Consult add(Consult consult);

    /**
     *
     * @param id
     * @param salesId 销售员id
     * @return
     */
    Consult allocate(String id,String salesId);

    /**
     * 修改信息
     * @param consult
     * @return
     */
    Consult modify(Consult consult);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    Consult findOne(String id);
}
