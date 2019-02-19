package com.qunchuang.carmall.service;


import com.qunchuang.carmall.domain.Advertisement;

/**
 * @author Curtain
 * @date 2018/3/14 10:44
 */
public interface AdvertisementService {

    /**
     * 添加一条广告
     * @param advertisement
     * @return
     */
    Advertisement add(Advertisement advertisement);

    /**
     * 修改一条广告
     * @param advertisement
     * @return
     */
    Advertisement modify(Advertisement advertisement);

    /**
     * 删除一条广告
     * @param id
     */
    Advertisement delete(String id);

    /**
     * 查找一条广告
     * @param id
     * @return
     */
    Advertisement findOne(String id);



}
