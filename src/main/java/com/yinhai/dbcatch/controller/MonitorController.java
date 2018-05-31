package com.yinhai.dbcatch.controller;

import com.yinhai.dbcatch.service.MonitorService;
import com.yinhai.dbcatch.vo.DatasourceVO;
import com.yinhai.dbcatch.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class MonitorController {
    @Autowired
    private MonitorService monitorService;


    @RequestMapping("/MonitorController/listRunMonitor")
    public ModelAndView listDatasource() {
        ModelAndView mav = new ModelAndView("datasource/RunMonitor");
        List<Map<String, Object>> list = monitorService.getRunInfo();
        mav.addObject("runlist",list);
        return mav;
    }

    @RequestMapping("/MonitorController/startRun")
    @ResponseBody
    public ResultVO startRun(DatasourceVO dsvo) {
        try {
            monitorService.startRun(dsvo);
            return new ResultVO("success");
        }catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }
    }

    @RequestMapping("/MonitorController/stopRun")
    @ResponseBody
    public ResultVO stopRun(DatasourceVO dsvo) {
        try {
            monitorService.stopRun(dsvo);
            return new ResultVO("success");
        }catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }
    }

    @RequestMapping("/MonitorController/getLog")
    @ResponseBody
    public ResultVO getLog(DatasourceVO dsvo) {
        try {
            String log = monitorService.getLog(dsvo);
            return new ResultVO(log);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }
    }



}
