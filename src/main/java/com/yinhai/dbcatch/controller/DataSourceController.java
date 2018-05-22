package com.yinhai.dbcatch.controller;

import com.yinhai.dbcatch.service.DataSourceService;
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
public class DataSourceController {
    @Autowired
    private DataSourceService dataSourceService;

    @RequestMapping("/datasource/SetSource")
    public String setSource() {
        return "datasource/SetSource";
    }

    @RequestMapping("/datasource/listDatasource")
    public ModelAndView listDatasource() {
        ModelAndView mav = new ModelAndView("datasource/SetSource");
        List<Map<String, Object>> list = dataSourceService.getAll();
        mav.addObject("dslist",list);
        return mav;
    }
    @RequestMapping("/datasource/deleteDb")
    @ResponseBody
    public  ResultVO deleteDb(DatasourceVO dsvo) {
        try {
            dataSourceService.deleteDb(dsvo);
            return new ResultVO("success");
        }catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }
    }

    @RequestMapping("/datasource/addDb")
    @ResponseBody
    public ResultVO addDb(DatasourceVO dsvo) {
        try {
            dataSourceService.addDb(dsvo);
            return new ResultVO("success");
        }catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }

    }
}
