package com.dynamic_validate.controller;

import com.dynamic_validate.dao.DemandInvokeDao;
import com.dynamic_validate.dao.DemandPathDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.DemandInvoke;
import com.dynamic_validate.entity.DemandPath;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.*;
import com.dynamic_validate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class CheckController {
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

    @GetMapping("/selfCheckList")
    public String selfCheckList() {
        samlListService.listCheck();
        return "m_success";
    }

    @GetMapping("/selfCheckTypeById")
    public String selfCheckTypeById(@RequestParam(value = "id") int id) {
        String rep_id = pro + "" + System.currentTimeMillis();
        SamlType type = samlTypeDao.findSamlTypeById(id);
        boolean flag = ruleService.DEV_SR(type, rep_id); // 这里其实已经更新了type的Vali了，下面没必要了。

        System.out.println(flag);
        //if (flag) {
        //    return Data.OK;
        //} else {
        //    return Data.Error;
        //}
        return "m_success";
    }

    @GetMapping("/selfCheckInclude")
    public String selfCheckInclude() {
        String rep_id = pro + "" + System.currentTimeMillis();
        System.out.println("======开始“include自检”======");
        // 1、先把带问号的相对路径补充完整【并且生成include_result，并没有】
        includeService.finishQuestExp(pro);
        // 2、生成name并找到origin，更新到include_import表中。
        includeService.generNameAndOrigin(Data.FUNC_DECLARE, pro); // 生成name 9。
        includeService.generNameAndOrigin(Data.STRUCT_DECLARE, pro); // 生成name 14。
        includeService.generIncludeOrigin(pro, rep_id); // 目前仅生成有的，不再copy了。
        //includeService.copyAllIncludeFile(pro, rep_id);

        System.out.println("======结束“include自检”======");
        return "m_success";

    }

    /**
     * @param level 参数
     *              level=-1，表示在type表的所有level上做自检
     *              如果1=<level<=7，表示在某个level上做自检
     * @return String 跳转到相应页面
     *
     * 【其实，后期需要在前台显示检测结果，那肯定就需要将检测信息收集起来，用map或list存储起来。】
     */
    @GetMapping("/selfCheckType")
    public String selfCheckType(@RequestParam(value = "level") int level) {
        String rep_id = pro + "" + System.currentTimeMillis();

        System.out.println("======开始“设计自检”======");
        if (level == -1) {
            //1没什么好查的，从2开始。【但是为了改变state状态，还是查查好了】
            for (int i = 1; i < 7; i++) {
                samlTypeService.typeCheckByLevel(i, pro, rep_id);
            }
        } else {
            samlTypeService.typeCheckByLevel(level, pro, rep_id);
        }
        System.out.println("======结束“设计自检”======");

        return "m_success";
    }

    /**
     * 例如：
     * t = {"小明"}，且level=1，name=String，一判断，“小明”就是String，应该返回true
     * t = {{"","",""},{},}且level=2（function），先判断father是不是interface类型的，再判断in_list和out_list是不是base_list，如果都是，那就是function
     * 【
     * 1.自检，模型结构，自顶向下
     * 似乎是检测“给出的类型是否正确”，也就是建模给的类型之间是否有矛盾。
     * 根本就不用给用例吧，自检就行了。。自己在数据库一个一个type比对不就完了？
     *
     * 感觉这一步检测的就是level是否正确，level正确时，具体的参数类型是否都存在，如果都存在，那就正确。
     * 其实，level就是关系了，但level只是层次关系，list中的那些也是Type，是半成品，并没有level属性，所以必须有关系字段呀！
     *
     *
     * 最顶层是level=7，是Mfrwk所包含的框架Mfrm，最底层是level=1，这一层是基本类型Integer、String等，可以直接进行类型判断。
     *
     * 顺序就是从level7开始往下一个一个过，
     * ①每一个Type都需要一轮儿递归？
     * ②还是整个系统递归一次就搞定了？【感觉是这个】
     * 【②应该不全吧？不参与层次结构的复合类型咋整？应该分开检测，一种是结构检测，就是这个②，一种是复合类型单独检测，就是①，一个一个过】
     *
     * 先写②：其实也就检测那个顶层Type就完了，只有一个。
     *
     *
     * 】
     *
     * 【
     * 2.检测实参，跑用例
     * 检测用例是否正确嘛，应该是系统跑起来，给的配置参数，即实参是否正确。
     *
     * 检测具体实例对象的逻辑：检测实参值是否正确
     * {,{"123"},{1}}，level=2，按理说，不用检测整个结构了，因为自检已经检测过了，只要给的参数对，而且上面的自检没出错，带进去结果一定对！
     *
     *
     * 】
     *
     */
    @GetMapping("/globalVerify")
    public String globalVerify(Model model) {
        String rep_id = pro + "" + System.currentTimeMillis();

        System.out.println("\n开始“一键验证”......\n");
        ruleService.VERI_Demand(pro, rep_id);// 用了规则公式写
        System.out.println("......结束“一键验证”\n");

        reportService.generReportStart(rep_id); //生成所有title的start位置。
        //reportService.generReportStart(rep_id); //尝试替换或者怎么处理一下之前已经产生过的家伙。

        Data.repId = rep_id;
        model.addAttribute("repId", rep_id);

        return "veri_reportShow";
    }

    /**
     * type，表示要验证的需求类型, invoke=0就是调用，path=1就是整个路径即调用列表。
     * id, 勾选的需求id。
     *
     * 获取当前毫秒数，作为这份验证报告的ID。
     */
    @GetMapping("/partVerify")
    public String partVerify(int type, int id) {
        String rep_id = pro + "" + System.currentTimeMillis();

        System.out.println("\n开始“局部验证”......\n");
        if (type == 0) {
            DemandInvoke d = demandInvokeDao.findById(id);
            ruleService.DEV_IR(d, rep_id);// 用了规则公式写
        } else if (type == 1) {
            DemandPath dp = demandPathDao.findById(id).get();
            ruleService.VERI_DP(dp, rep_id, pro);
        }
        System.out.println("结束“局部验证”\n");

        return "m_success";
    }

}
