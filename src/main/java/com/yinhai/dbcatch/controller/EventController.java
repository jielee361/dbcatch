package com.yinhai.dbcatch.controller;

import com.yinhai.dbcatch.service.DataSourceService;
import com.yinhai.dbcatch.service.EventService;
import com.yinhai.dbcatch.vo.DatasourceVO;
import com.yinhai.dbcatch.vo.EventVO;
import com.yinhai.dbcatch.vo.ResultKV;
import com.yinhai.dbcatch.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.awt.*;
import java.util.List;
import java.util.Map;

@Controller
public class EventController {
    @Autowired
    private EventService eventService;

    @RequestMapping("/event/listEvent")
    public ModelAndView listEvent() {
        ModelAndView mav = new ModelAndView("event/SetEvent");
        List<Map<String, Object>> list = eventService.getAll();
        mav.addObject("evtlist", list);
        return mav;
    }

    @RequestMapping("/event/addEvent")
    public String addEvent() {
        return "/event/AddEvent";
    }

    @RequestMapping("/event/getDatasource")
    @ResponseBody
    public ResultVO getDatasource() {
        try {
            ResultKV[] dss = eventService.getDatasource();
            return new ResultVO(dss);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }

    }

    @RequestMapping("/event/getTableName")
    @ResponseBody
    public ResultVO getTableName(DatasourceVO dsvo) {
        try {
            ResultKV[] tableNames = eventService.getTableName(dsvo);
            return new ResultVO(tableNames);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }

    }

    @RequestMapping("/event/getTableCol")
    @ResponseBody
    public ResultVO getTableCol(EventVO evo) {
        try {
            ResultKV[] tableCol = eventService.getTableCol(evo);
            return new ResultVO(tableCol);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }

    }

    @RequestMapping("/event/add")
    @ResponseBody
    public ResultVO add(EventVO evo) {
        try {
            eventService.addEvent(evo);
            return new ResultVO("");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }
    }

    @RequestMapping("/event/delete")
    @ResponseBody
    public ResultVO delete(EventVO evo) {
        try {
            eventService.deleteEvent(evo);
            return new ResultVO("");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(e);
        }
    }



}
