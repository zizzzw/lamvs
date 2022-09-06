package com.example.dynamic_validate.controller;

import com.example.dynamic_validate.dao.SamlRelationClassifyDao;
import com.example.dynamic_validate.dao.SamlRelationDao;
import com.example.dynamic_validate.dao.SamlTypeDao;
import com.example.dynamic_validate.entity.SamlRelation;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ManageRelationController {
    @Autowired
    private SamlRelationDao samlRelationDao;
    @Autowired
    private SamlRelationClassifyDao classifyDao;
    @Autowired
    private SamlTypeDao samlTypeDao;

    @GetMapping("/showRelation")
    @ResponseBody
    public Map<String, Object> showRelation(@RequestParam(value = "pageIndex") int pageIndex, @RequestParam(value = "pageSize") int pageSize) {
        Map<String, Object> map = new HashMap<>();

        //List<RelationPojo> rl = new ArrayList<>();
        //List<SamlRelation> list = samlRelationDao.findAll();
        //
        //for (SamlRelation r : list) {
        //    String name = classifyDao.findById(r.getRelationClassify()).get().getName();
        //    if (r.getRelationClassify() == 2) {
        //        String field1 = samlTypeDao.findById(r.getField1()).get().getExp();
        //        String field2 = samlTypeDao.findById(r.getField2()).get().getExp();
        //        rl.add(new RelationPojo(name, field1, field2, r.getField2Classify()));
        //    }
        //}

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<SamlRelation> relationPage = samlRelationDao.findAll(pageable);
        System.out.println(pageIndex + "=========目标页面");
        System.out.println(relationPage);
        map.put("relationPage", relationPage);

        return map;
    }

}
