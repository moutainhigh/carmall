package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2019/1/21 11:17
 */
@RestController
@RequestMapping("/init")
public class InitRestController {

    @Autowired
    private AdminService adminService;

    @RequestMapping("/accout")
    public String account(String curtain){
        return adminService.init(curtain);
    }
}
