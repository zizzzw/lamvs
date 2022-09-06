package com.example.dynamic_validate.controller;

import com.example.dynamic_validate.dao.SamlTypeMacroDao;
import com.example.dynamic_validate.service.DemandInvokeService;
import com.example.dynamic_validate.service.GenerateListService;
import com.example.dynamic_validate.service.GenerateRelationService;
import com.example.dynamic_validate.service.GenerateTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class AutoCheckController {
    @Value("${lamvs.project}")
    int pro;
    @Autowired
    private GenerateTypeService generateTypeService;
    @Autowired
    private GenerateListService generateListService;
    @Autowired
    private DemandInvokeService demandInvokeService;
    @Autowired
    private GenerateRelationService generateRelationService;
    @Autowired
    private SamlTypeMacroDao samlTypeMacroDao;
    @Autowired
    private CheckController checkController;
    @Autowired
    private DataImportController dataImportController;
    @Autowired
    private GenerateController generateController;

    @GetMapping("/autoVerify")
    public String autoVerify(Model model) {
        String re;

        // 一、 清空表
        //samlTypeMacroDao.truncateTable();
        //samlTypeMacroDao.initVerify();

        // 二、自动导入并验证

        //1 从代码导入type表
        //http://localhost:8080/importDesign
        re = dataImportController.importDesign();
        System.out.println("从代码导入type表，成功！");

        //2 完善Type和List表
        //http://localhost:8080/finishType
        re = generateController.finishType();
        System.out.println("完善Type和List表，成功！");

        //3 导入demand的txt
        //http://localhost:8080/importDemand
        //re = dataImportController.importDemand();
        //if (re.equals("success")) {
        //    System.out.println("导入demand的txt，成功！");
        //}

        //4 完善Demand表
        //http://localhost:8080/finishDemand
        re = generateController.finishDemand();
        System.out.println("完善Demand表，成功！");

        //5 关系生成
        //http://localhost:8080/relationGenerate?relationClassify=-1
        re = generateController.relationGenerate(-1);
        System.out.println("关系生成，成功！");

        //6 include文件自检+完善【调用验证需要】
        //http://localhost:8080/selfCheckInclude
        //re = checkController.selfCheckInclude();
        //System.out.println("include文件自检+完善，成功！");

        //去重
        //re = generateController.distinctTypeAndInclude();
        //System.out.println("去重，成功！");

        //7 一键验证+报告
        //http://localhost:8080/globalVerify
        re = checkController.globalVerify(model);
        System.out.println("一键验证+报告，成功！");

        return "veri_reportShow";
    }

}
