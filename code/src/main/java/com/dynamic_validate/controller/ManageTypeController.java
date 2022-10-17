package com.dynamic_validate.controller;

import com.dynamic_validate.dao.*;
import com.dynamic_validate.dao.*;
import com.dynamic_validate.entity.Project;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.entity.SamlTypeLevel;
import com.dynamic_validate.entity.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class ManageTypeController {
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


    /**
     * typeManage的入口
     * 查询所有下拉列表SelectList的内容，用Model
     */
    // @GetMapping("/showTypeSelectList")
    @GetMapping("/typeManage")
    public String showTypeSelectList(Model model) {
        List<Project> allProject = projectDao.findAll();
        List<Source> allSource = sourceDao.findAll();
        List<SamlTypeLevel> allSamlTypeLevel = levelSamlDao.findAll();
        model.addAttribute("allProject", allProject);
        model.addAttribute("allSource", allSource);
        model.addAttribute("allSamlTypeLevel", allSamlTypeLevel);
        return "type_manage";
    }


    @GetMapping("/showType")
    @ResponseBody
    public Map<String, Object> showType(@RequestParam(value = "pageIndex") int pageIndex, @RequestParam(value = "pageSize") int pageSize) {
        Map<String, Object> map = new HashMap<>();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<SamlType> typePage = samlTypeDao.findAll(pageable);
        //Page<SamlType> typePage = samlTypeDao.findOpen(pro, pageable);
        System.out.println(pageIndex + "=========目标页面");
        System.out.println(typePage);
        map.put("typePage", typePage);

        return map;
    }


    @PostMapping("/addType")
    public String addType(SamlType samlType) throws Exception {
        System.out.println(samlType.getExp());
        //save之前，不检查一下，是否有问题吗？感觉不能校验，只能全部添加后再校验，否则太耽误时间。
        samlTypeDao.save(samlType);
        // int fatherListId = samlListDao.save(fatherListMembers, memberClassify, remark);//参考这个写法。

        return "redirect:/type_manage";
    }


    @PostMapping("/deleteType")
    public String deleteType(String typeList) throws Exception {
        String[] strs = typeList.split(",");
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < strs.length; i++) {
            ids.add(Integer.parseInt(strs[i]));
        }

        // userInfoService.batchDelete(ids);

        // samlTypeDao.deleteById(ids[0]);

        return "redirect:/type_manage";
    }
}
