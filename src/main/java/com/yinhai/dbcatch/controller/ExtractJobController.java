package com.yinhai.dbcatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ExtractJobController {
    @RequestMapping("/extractJob/listAll")
    public ModelAndView listAll() {
        ModelAndView mav = new ModelAndView("extractjob/jobCfg");
        return mav;
    }

}
