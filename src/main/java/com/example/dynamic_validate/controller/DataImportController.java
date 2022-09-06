package com.example.dynamic_validate.controller;

import com.example.dynamic_validate.dao.SamlTypeDao;
import com.example.dynamic_validate.data.Data;
import com.example.dynamic_validate.service.DataImportService;
import com.example.dynamic_validate.service.GenerateTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**仅可执行一次的*/
@Controller
@AutoConfigurationPackage
@RequestMapping("/")
public class DataImportController {
    @Value("${lamvs.project}")
    int pro;

    @Autowired
    private DataImportService dataImportService;
    @Autowired
    private GenerateTypeService generateTypeService;
    @Autowired
    private SamlTypeDao samlTypeDao;


    /**
     * 需求建模，仅在最初生成数据时执行一次。
     *
     * v2,导入需求：从需求demandDentry导入所有txt文件到demand_path表中。【注意project！】
     *
     * 验证完整步骤————导入需求+验证：
     *      1、导入需求：从需求demandDentry导入所有txt文件到demand_path表中。【注意project！】
     *      2、验证：
     *         (1)验证前提：完善demand_path表中的func1和func2字段。
     *         (2)生成依赖类型，验证类型
     *         (3)生成关系，验证关系
     *
     *
     * v1,导入需求：仅在最初生成数据时执行一次，因为是Excel的原因，否则会出问题。
     *      1、从树形调用文本的Excel(需求规范模板.xlsx)中生成调用关系(saml_demand.xlsx)。
     *      2、完善调用关系的demand_path.xlsx文件：finishDemandExcel()
     *      3、生成结构关系【啊这个Excel还得改，再说】
     *      4、生成类型【啊这个Excel还得改，再说】
     */
    @GetMapping("/importDemand")
    public String importDemand() {
        //dataImportService.demandTree2SamlDemand(); //目前没有需求树，再说吧。
        //dataImportService.finishDemandExcel(Data.demandExcel);//一次性，不通用
        String sour = "";
        switch (pro) {
            case Data.Pro6:
                sour = Data.d_vfs;
                break;
            case Data.Pro7:
                sour = Data.d_guest;
                break;
            case Data.Pro8:
                sour = Data.d_host;
                break;
            case Data.Pro9:
                sour = Data.fs_pguestfs;
                break;
        }
        dataImportService.importDemandTxt(sour, pro);

        return "m_success";
    }

    @GetMapping("/importDesign")
    public String importDesign() {
        if (!samlTypeDao.existsById(1)) { // 如果没有base，先引入
            samlTypeDao.insertBase();
        }
        //String sour = "";
        //switch (pro) {
        //    case Data.Pro6:
        //        sour = Data.source_linux;
        //        break;
        //    case Data.Pro7:
        //        sour = Data.source_guest;
        //        break;
        //    case Data.Pro8:
        //        sour = Data.source_host;
        //        break;
        //    //case Data.Pro9:
        //    //    sour = Data.source_fs_pguestfs;
        //    //    break;
        //}
        //dataImportService.importDesignFromSource(sour, pro);

        generateTypeService.generNameByExp(pro); // 生成name，主要是结构体内的函数声明。
        generateTypeService.generLevel4_6ByPath(pro); //生成level4-6，以及father

        return "m_success";
    }
}
