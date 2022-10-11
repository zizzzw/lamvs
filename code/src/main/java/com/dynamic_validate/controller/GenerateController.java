package com.dynamic_validate.controller;

import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.service.*;
import com.example.dynamic_validate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**可执行多次的*/
@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class GenerateController {
    @Value("${lamvs.project}")
    private int pro;

    @Autowired
    private GenerateRelationService generateRelationService;

    @Autowired
    private SamlTypeService samlTypeService;

    @Autowired
    private SamlListService samlListService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private DEVService devService;
    @Autowired
    private GenerateTypeService generateTypeService;
    @Autowired
    private GenerateListService generateListService;
    @Autowired
    private DemandInvokeService demandInvokeService;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private ReportService reportService;


    /**
     * 可执行多次。
     *
     * 完善type表：
     *      1、根据type表，生成需要的list1和list2；完善list1和list2字段。
     *      2、
     *
     */
    @GetMapping("/finishType")
    public String finishType() {
        generateListService.generListByExp(pro); //生成：name,list1,list2
        //generateTypeService.dealTypedef(pro);
        generateTypeService.generfuncPointerNameByExp(pro); // 生成指针函数的名字。【结构体成员函数、参数指针函数都是】
        samlTypeDao.updateSubsyFather();//不知道什么原因导致/的father是自己，直接更新成0算啦
        return "import_design";
    }

    @GetMapping("/distinctTypeAndInclude")
    public String distinctTypeAndInclude() {
        //demandInvokeService.demandDistinct(); // demand表去重
        generateTypeService.typeDistinctByExp(); // type表去重
        //generateTypeService.includeDistinct(); // type表去重【dao内存泄露，导致distinc和generIncludeName不能一起使用】
        return "m_success";
    }

    @GetMapping("/updateList4_6")
    @Deprecated
    public String updateList4_6() {
        generateTypeService.generLevel4_6ByPath(pro);
        generateTypeService.generIncludeName(); // 给结构体声明和函数声明生成名字。【临时的，实际上selfCheckInclude包含了】

        return "m_success";
    }

    @GetMapping("/finishDemand")
    public String finishDemand() {
        demandInvokeService.finishDemandFuncs(); //生成需求的两个函数
        return "import_demand";
    }

    @GetMapping("/relationGenerate")
    public String relationGenerate(@RequestParam(value = "relationClassify") int relationClassify) {
        if (relationClassify == -1) {
            generateRelationService.generateAggregation(); //2
            generateRelationService.generateParaAssociation(); //4,5
            generateRelationService.generateVarFunction(); // 6,7
            generateRelationService.generateFuncStructFile(); // 11,12
        } else if (relationClassify == Data.SubType) {//1,子类型关系
            generateRelationService.generateSubType();
        } else if (relationClassify == Data.Aggregation) {//2,聚合关系
            generateRelationService.generateAggregation();
        } else if (relationClassify == Data.TypeAssociation) {//3,类关联关系
            generateRelationService.generateTypeAssociation();
        } else if (relationClassify == Data.ParamIn ||
                relationClassify == Data.ParamOut) {//4,输入参数关联；5,输出参数关联
            generateRelationService.generateParaAssociation();
        } else if (relationClassify == Data.VarStruct ||
                relationClassify == Data.FuncStruct) {//6,成员变量关联；7,成员函数关联
            generateRelationService.generateVarFunction();
        } else if (relationClassify == Data.Accessible) {//8.Accessible 可访问
            generateRelationService.generateAccessible();
        } else if (relationClassify == Data.Invocable) {//9.Invocable 函数可调用
            generateRelationService.generateInvocable();
        } else if (relationClassify == Data.ParamInvocable) {//10.ParamInvocable 可传参调用
            generateRelationService.generateParamInvocable();
        } else if (relationClassify == Data.FuncFile) { //11,FuncFile 函数文件关联，全局函数 12,StructFile 结构体文件
            generateRelationService.generateFuncStructFile();
        }

        return "import_design";
    }

    @GetMapping("/generReportFather")
    public String generReportFather(@RequestParam(value = "repId") String repId) {
        reportService.generReportStart(repId); //生成所有title的start位置。
        return "m_success";
    }
}
