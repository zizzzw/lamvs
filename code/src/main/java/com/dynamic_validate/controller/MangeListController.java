package com.dynamic_validate.controller;

import com.dynamic_validate.dao.SamlListDao;
import com.example.dynamic_validate.dao.*;
import com.dynamic_validate.entity.SamlList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class MangeListController {
    @Autowired
    private SamlListDao samlListDao;

    //allType
    //allCompound

    @GetMapping("/showList")
    @ResponseBody
    public Map<String, Object> showList(@RequestParam(value = "pageIndex") int pageIndex, @RequestParam(value = "pageSize") int pageSize) {
        Map<String, Object> map = new HashMap<>();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<SamlList> listPage = samlListDao.findAll(pageable);
        System.out.println(listPage.getSize());
        //System.out.println(listPage.getContent().get(0).getMembers());
        map.put("listPage", listPage);
        return map;
    }


    @PostMapping("/addList")
    public String addList(SamlList samlList) throws Exception {
        System.out.println(samlList.getExp());
        //save之前，不检查一下，是否有问题吗？感觉不能校验，只能全部添加后再校验，否则太耽误时间。
        samlListDao.save(samlList);
        // int fatherListId = samlListDao.save(fatherListMembers, memberClassify, remark);//参考这个写法。

        return "redirect:/list_manage";
    }

}
