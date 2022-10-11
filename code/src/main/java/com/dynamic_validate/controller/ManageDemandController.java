package com.dynamic_validate.controller;

import com.dynamic_validate.dao.*;
import com.example.dynamic_validate.dao.*;
import com.dynamic_validate.entity.DemandInvoke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class ManageDemandController {
    @Value("${lamvs.project}")
    int pro;
    @Autowired
    private SamlTypeDao samlTypeDao;

    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private SourceDao sourceDao;
    @Autowired
    private SamlTypeLevelDao levelSamlDao;
    @Autowired
    private DemandInvokeDao demandInvokeDao;


    @GetMapping("/showDemandInvoke")
    @ResponseBody
    public Map<String, Object> showDemandInvoke(@RequestParam(value = "pageIndex") int pageIndex, @RequestParam(value = "pageSize") int pageSize) {
        Map<String, Object> map = new HashMap<>();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<DemandInvoke> demandPage = demandInvokeDao.findByProject(pro, pageable);

        System.out.println(pageIndex + "=========目标页面");
        System.out.println(demandPage);
        map.put("demandPage", demandPage);

        return map;
    }

}
