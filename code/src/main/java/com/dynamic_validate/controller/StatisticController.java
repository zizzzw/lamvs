package com.dynamic_validate.controller;

import com.dynamic_validate.dao.DemandInvokeDao;
import com.dynamic_validate.dao.DemandPathDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.*;
import com.example.dynamic_validate.service.*;
import com.dynamic_validate.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class StatisticController {
    @Value("${lamvs.project}")
    int pro;

    @Autowired
    private SamlTypeService samlTypeService;
    @Autowired
    private SamlListService samlListService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private DemandInvokeDao demandInvokeDao;
    @Autowired
    private DemandPathDao demandPathDao;
    @Autowired
    private IncludeService includeService;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private StatisticService statisticService;

    @GetMapping("/readStatis")
    @ResponseBody
    public Map<String, Object> readStatis() {
        Map<String, Object> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(samlTypeDao.findCount(pro, 1));
        list.add(samlTypeDao.findCount(pro, 2));
        list.add(samlTypeDao.findCount(pro, 3));
        list.add(samlTypeDao.findCount(pro, 4));
        list.add(samlTypeDao.findCount(pro, 5));
        list.add(samlTypeDao.findCount(pro, 6));
        map.put("listCount", list);

        return map;
    }

    @GetMapping("/dealDemand")
    public String dealDemand() {
        StringBuilder filst = statisticService.getAllFunc();
        System.out.println("依赖函数id：" + filst);
        List<SamlType> dpList = statisticService.getAllDepends();

        StringBuilder dpids = new StringBuilder();
        for (SamlType t : dpList) {
            dpids.append(t.getId() + ",");
        }

        System.out.println("函数依赖类型id：" + dpids);
        List<List<SamlType>> dpListByLevel = statisticService.splitByLevel(dpList);
        List<SamlType> baseList = dpListByLevel.get(0);
        List<SamlType> funcList = dpListByLevel.get(1);
        List<SamlType> structList = dpListByLevel.get(2);
        List<SamlType> otherList = dpListByLevel.get(3);

        List<String> tdpList = statisticService.getTypeDeps(dpList);
        System.out.println("存在即正确的类型，没有依赖：" + tdpList.get(0));
        System.out.println("有依赖的类型：" + tdpList.get(1));
        System.out.println("这些类型的依赖类型：" + tdpList.get(2));

        List<String> reList = statisticService.getReList(tdpList.get(2));
        System.out.println("存在的依赖类型：" + reList.get(0));
        System.out.println("重复的依赖类型：" + reList.get(1));
        System.out.println("缺失的依赖类型：" + reList.get(2));
        System.out.println("依赖类型ids：" + reList.get(3));
        //String ids_inner = StrUtil.distinct(reList.get(2).replaceAll("(struct)|\\{|\\}", ""));
        String ids_re = StrUtil.distinct(String.valueOf(filst.append(dpids).append(reList.get(3) + ",").append(reList.get(2))));
        System.out.println("需求涉及的全部类型：" + ids_re);
        statisticService.copyAllDps(ids_re);
        //statisticService.generFiles(flist);

        return "m_success";
    }
}
