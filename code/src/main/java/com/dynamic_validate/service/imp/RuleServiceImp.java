package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.*;
import com.dynamic_validate.entity.*;
import com.dynamic_validate.service.*;
import com.example.dynamic_validate.dao.*;
import com.dynamic_validate.data.Data;
import com.example.dynamic_validate.entity.*;
import com.example.dynamic_validate.service.*;
import com.dynamic_validate.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RuleServiceImp implements RuleService {
    @Autowired
    private DemandPathDao demandPathDao;
    @Autowired
    private DemandInvokeDao demandInvokeDao;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private SamlRelationDao samlRelationDao;
    @Autowired
    private DataImportService dataImportService;
    @Autowired
    private SamlTypeService samlTypeService;
    @Autowired
    private DemandInvokeService demandInvokeService;
    @Autowired
    private RuleStructService ruleStructService;
    @Autowired
    private RuleExistService ruleExistService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ErrorReportDao errorReportDao;
    @Autowired
    private IncludeImportDao includeImportDao;
    @Autowired
    private IncludeResultDao includeResultDao;


    /**
     * 暂时先不对Demand处理，就到demandPath层，Demand只是为了防止空指针而已。
     */
    @Override
    public void VERI_Demand(int pro, String rep_id) {
        //List<DemandPath> demand = demandPathDao.findByProjectAndState(pro, Data.State);
        List<DemandPath> demand = demandPathDao.findByProject(pro);

        boolean d_flag = true;
        for (DemandPath dp : demand) {
            //boolean dp_flag = VERI_DP(dp, rep_id, pro);
            boolean dp_flag = VERI_DPForRep(dp, rep_id, pro);

            d_flag = d_flag && dp_flag;
        }
        Demand d = new Demand();
        errorDeal(d_flag, Data.Demand, d, Data.DTitle, rep_id);
    }


    private boolean VERI_DPForRep(DemandPath dp, String rep_id, int pro) {
        System.out.println("========开始：验证需求路径" + dp.getName() + "=======");
        List<DemandInvoke> diList = new ArrayList<>();
        diList = demandInvokeDao.findByProject(pro);
        //diList = demandInvokeDao.findByValid0(pro);

        //List<DemandInvoke> diList = new ArrayList<>();
        //DemandInvoke demand = demandInvokeDao.findById(970).get();
        //diList.add(demand);

        boolean dp_flag = true;
        for (DemandInvoke di : diList) {
            System.out.println("=======开始：验证需求调用(2函数+依赖+调用)" + di.getExp() + "=======");
            boolean di_flag;
            // 1 2个函数正确（意味着依赖类型存在） f1_flag f2_flag
            // 2 依赖类型正确  f1_deps_flag  f2_deps_flag
            //                    ti_flag     ti_flag
            // 3 调用关系正确  f12_inv_flag
            List<SamlType> funcList = getFuncList(di, rep_id); //两个函数一定存在
            System.out.println("=======开始：先验证两个函数的结构正确性，如果不通过，依赖列表都不用生成了。=======");
            SamlType f1 = funcList.get(0);
            SamlType f2 = funcList.get(1);
            boolean f1_flag = DEV_SR_2Func(f1, rep_id); //先验证两个函数的结构正确性，如果不通过，依赖列表都不用生成了。
            boolean f2_flag = DEV_SR_2Func(f2, rep_id);
            di_flag = f1_flag && f2_flag;
            System.out.println("=======结束：验证两个函数的结构正确性，" + di_flag + "=======");

            if (!di_flag) {
                errorDeal(di_flag, Data.DemandInvoke, di, Data.DITitle, rep_id);
                demandInvokeDao.updateValid(di.getId(), 0);
                System.out.println("=======【提前】结束：验证需求调用(2函数+依赖+调用)" + di.getExp() + "，false=======");
                continue;
            }
            boolean f1_deps_flag = checkDepends(f1, rep_id);
            boolean f2_deps_flag = checkDepends(f2, rep_id);
            di_flag = f1_deps_flag && f2_deps_flag;

            if (!di_flag) {
                errorDeal(di_flag, Data.DemandInvoke, di, Data.DITitle, rep_id);
                demandInvokeDao.updateValid(di.getId(), 0);
                System.out.println("=======【提前】结束：验证需求调用(2函数+依赖+调用)" + di.getExp() + "，false=======");
                continue;
            }

            di_flag = DEV_IR(di, rep_id); // f12_inv_flag，仅调用的真假

            errorDeal(di_flag, Data.DemandInvoke, di, Data.DITitle, rep_id); //(2函数+依赖+调用)
            int vali = di_flag ? 1 : 0;
            demandInvokeDao.updateValid(di.getId(), vali);
            System.out.println("=======结束：验证需求调用(2函数+依赖+调用)" + di.getExp() + "，" + di_flag + "=======");
            dp_flag = dp_flag && di_flag;
        }
        errorDeal(dp_flag, Data.DemandPath, dp, Data.DPTitle, rep_id);
        System.out.println("========结束：验证需求路径" + dp.getName() + "，" + dp_flag + "=======");
        return dp_flag;
    }


    @Value("${veri.global.di.fromFirstNotdeal}")
    private boolean fromFirstNotdeal;

    /**
     * 整个系统的验证逻辑：
     *  1、首先获取待验证需求功能对应的需求列表, 遍历整个需求列表,
     *  2、对每个需求提取其包含的函数,(2个函数，主调和被调)
     *  3、再提取每个函数的依赖类型,
     *  4、对每个类型进行类型存在性和类型结构验证,
     *  5、如果每个函数的每个类型都通过了类型验证, 则表示当前这个需求通过了类型验证,
     *  6、再对其进行调用关系验证, 如果所有的需求都通过了调用关系验证,
     *     则整个需求列表代表的功能正确, 即设计满足需求对该功能的要求.
     */
    @Override
    public boolean VERI_DP(DemandPath dp, String rep_id, int pro) {
        System.out.println("========开始：验证需求路径" + dp.getName() + "=======");
        List<DemandInvoke> diList;
        if (fromFirstNotdeal) {
            diList = demandInvokeDao.findByProjectLastWrong(pro);
        } else {
            diList = demandInvokeDao.findByProject(pro);
        }
        //List<DemandInvoke> diList = new ArrayList<>();
        //DemandInvoke demand = demandInvokeDao.findById(48475).get();
        //diList.add(demand);

        boolean dp_flag = true;
        for (DemandInvoke di : diList) {
            System.out.println("=======开始：验证需求调用(2函数+依赖+调用)" + di.getExp() + "=======");
            //ErrorReport dier = new ErrorReport(); // 这个瞎弄可害惨我喽
            //errorReportDao.save(dier);

            boolean di_flag;
            int vali = di.getValid(); // 加valid存储
            if (!openValid) { // 关掉状态判断，要不然报告就有问题了。
                vali = Data.Valid2;
            }
            if (vali == Data.Valid0) {
                di_flag = false;
                System.out.println("=======【已验】结束：验证需求调用(2函数+依赖+调用)" + di.getExp() + "，false=======");
            } else if (vali == Data.Valid1) {
                di_flag = true;
                System.out.println("=======【已验】结束：验证需求调用(2函数+依赖+调用)" + di.getExp() + "，true=======");
            } else { // 未验证过
                // 1 2个函数正确（意味着依赖类型存在） f1_flag f2_flag
                // 2 依赖类型正确  f1_deps_flag  f2_deps_flag
                //                    ti_flag     ti_flag
                // 3 调用关系正确  f12_inv_flag
                List<SamlType> funcList = getFuncList(di, rep_id); //两个函数一定存在
                System.out.println("=======开始：先验证两个函数的结构正确性，如果不通过，依赖列表都不用生成了。=======");
                SamlType f1 = funcList.get(0);
                SamlType f2 = funcList.get(1);
                boolean f1_flag = DEV_SR_2Func(f1, rep_id); //先验证两个函数的结构正确性，如果不通过，依赖列表都不用生成了。
                boolean f2_flag = DEV_SR_2Func(f2, rep_id);
                di_flag = f1_flag && f2_flag;
                System.out.println("=======结束：验证两个函数的结构正确性，" + di_flag + "=======");

                if (!di_flag) {
                    errorDeal(di_flag, Data.DemandInvoke, di, Data.DITitle, rep_id);
                    demandInvokeDao.updateValid(di.getId(), 0);
                    System.out.println("=======【提前】结束：验证需求调用(2函数+依赖+调用)" + di.getExp() + "，false=======");
                    continue;
                }
                boolean f1_deps_flag = checkDepends(f1, rep_id);
                boolean f2_deps_flag = checkDepends(f2, rep_id);
                di_flag = f1_deps_flag && f2_deps_flag;

                if (!di_flag) {
                    errorDeal(di_flag, Data.DemandInvoke, di, Data.DITitle, rep_id);
                    demandInvokeDao.updateValid(di.getId(), 0);
                    System.out.println("=======【提前】结束：验证需求调用(2函数+依赖+调用)" + di.getExp() + "，false=======");
                    continue;
                }

                di_flag = DEV_IR(di, rep_id); // f12_inv_flag，仅调用的真假

                errorDeal(di_flag, Data.DemandInvoke, di, Data.DITitle, rep_id); //(2函数+依赖+调用)
                vali = di_flag ? 1 : 0;
                demandInvokeDao.updateValid(di.getId(), vali);
                System.out.println("=======结束：验证需求调用(2函数+依赖+调用)" + di.getExp() + "，" + di_flag + "=======");
            }

            dp_flag = dp_flag && di_flag;
        }

        errorDeal(dp_flag, Data.DemandPath, dp, Data.DPTitle, rep_id);
        System.out.println("========结束：验证需求路径" + dp.getName() + "，" + dp_flag + "=======");
        return dp_flag;
    }


    /**
     * 验证一个函数的依赖列表，返回真假。
     */
    private boolean checkDepends(SamlType f, String rep_id) {
        boolean fi_deps_flag = true;
        System.out.println("========开始：验证函数" + f.getName() + "的依赖类型=======");
        List<Integer> typeList = getDependTypeList(f); //1、获取函数依赖类型列表，不包括函数自身，不会出错。
        System.out.println(typeList + "这是函数" + f.getName() + "的依赖id，不应该有重复的！");
        // typeList有重复数据，需要根据id/name过滤一下，直接去报告中查，如果没有验过，再验证，验过的直接返回结果得了。
        for (int tid : typeList) {
            if (tid == f.getId()) continue; // 函数自身已经验过了，不用验证了。
            SamlType t = samlTypeDao.findSamlTypeById(tid);
            List<ErrorReport> tlist = errorReportDao.findType(tid, t.getName(), rep_id);
            boolean t_flag;
            if (tlist != null && !tlist.isEmpty()) {
                System.out.println("----------开始:验证类型" + t.getName() + "的正确性----------");
                ErrorReport tok = tlist.get(0);
                String errCode = tok.getErrorClassify();
                if (errCode.matches("5\\d{2}_1")) {
                    t_flag = true;
                } else { //"50\\d_2"
                    t_flag = false;
                }
                reportService.saveError(tok);
                System.out.println("----------【已验】结束:验证类型" + t.getName() + "的正确性:" + t_flag + "----------");
            } else {
                t_flag = DEV_SR(t, rep_id); //2、再一个个检查类型结构！！！
            }
            fi_deps_flag = fi_deps_flag && t_flag;
        }
        errorDeal(fi_deps_flag, Data.FuncDepend, f, Data.FuncDepTitle, rep_id);
        System.out.println("========结束：验证函数" + f.getName() + "的依赖类型，" + fi_deps_flag + "=======");
        return fi_deps_flag;
    }

    @Deprecated
    public boolean VERI_DP1(DemandPath dp, String rep_id, int pro) {
        System.out.println("========开始：验证需求路径" + dp.getName() + "=======");

        //List<DemandInvoke> diList = demandInvokeDao.findByDemandPath(dp.getId());//暂时不弄了，就是验证全部的Invoke得了。
        List<DemandInvoke> diList = demandInvokeDao.findByProject(pro);


        boolean dp_flag = true;
        for (DemandInvoke di : diList) {
            boolean di_flag = false;
            ////boolean di_flag = di.getValid() == Data.Valid; // 直接读库
            //boolean di_flag = VERI_DP(di, errList); //生成valid
            //验证调用关系的有效性，结果存到valid。
            // 1 2个函数正确（意味着依赖类型存在） f1_flag f2_flag
            // 2 依赖类型正确  f1_deps_flag  f2_deps_flag
            //                    ti_flag     ti_flag
            // 3 调用关系正确  f12_inv_flag
            System.out.println("=======开始：需求调用" + di.getExp() + "的验证=======");
            List<SamlType> funcList = getFuncList(di, rep_id);
            if (funcList == null) { // 两个函数不都存在，调用无效
                System.out.println("这个就不会出现，因为验的就是有的函数，没有的就没验证！");
                reportService.saveError(Data.FuncLackError, di, rep_id);
                dp_flag = false;
                System.out.println("=======结束：需求调用" + di.getExp() + "的验证，FuncLackError=======");
                continue;
            }
            System.out.println("=======开始：先验证两个函数的结构正确性，如果不通过，依赖列表都不用生成了。=======");
            boolean f1_flag = DEV_SR(funcList.get(0), rep_id); //先验证两个函数的结构正确性，如果不通过，依赖列表都不用生成了。
            boolean f2_flag = DEV_SR(funcList.get(1), rep_id);
            if (!f1_flag || !f2_flag) {
                dp_flag = false;
                System.out.println("=======结束：验证两个函数的结构正确性，" + dp_flag + "=======");
                continue;
            }
            System.out.println("=======结束：先验证两个函数的结构正确性，" + dp_flag + "=======");

            boolean fList_flag = true;
            for (SamlType f : funcList) {
                //boolean func_flag = f.getValid(); // 就是VERI_FUNC的结果
                System.out.println("========开始：验证函数" + f.getName() + "的依赖类型=======");
                boolean f_flag = true;
                List<Integer> typeList = getDependTypeList(f); //1、获取函数依赖类型列表，包括函数自身。错误打印。
                // typeList有重复数据，需要根据id/name过滤一下，直接去报告中查，如果没有验过，再验证，验过的直接返回结果得了。
                for (int tid : typeList) {
                    SamlType t = samlTypeDao.findSamlTypeById(tid);
                    ErrorReport tok = errorReportDao.findByTypeIdAndTypeNameAndReportId(tid, t.getName(), rep_id);
                    boolean t_flag;
                    if (tok != null && tok.getErrorClassify().matches("50\\d_\\d")) {
                        String errCode = tok.getErrorClassify();
                        if (errCode.matches("50\\d_1")) {
                            t_flag = true;
                        } else { //"50\\d_2"
                            t_flag = false;
                        }
                    } else {
                        t_flag = DEV_SR(t, rep_id); //2、再一个个检查类型结构！！！
                    }
                    f_flag = f_flag && t_flag;

                }
                errorDeal(f_flag, Data.FuncDepend, f, rep_id);
                System.out.println("========结束：验证函数" + f.getName() + "的依赖类型，" + f_flag + "=======");

                fList_flag = fList_flag && f_flag;
            }

            //boolean fi_flag = false;
            //if (fList_flag) {
            //    fi_flag = DEV_IR(di);
            //    errorDeal(fi_flag, Data.DemandInvoke, di); //调用
            //} else {
            //    System.out.println("依赖类型不存在，没必要验证调用了。");
            //    System.out.println("=======结束：需求调用" + di.getExp() + "的验证=======");
            //}
            di_flag = fList_flag && DEV_IR(di, rep_id); // 有短路求值, 如果fList_flag为false, 根本就不会运行DEV_IR！
            errorDeal(di_flag, Data.DemandInvoke, di, rep_id);// 依赖+函数调用(不是纯函数调用！)
            System.out.println("=======开始：需求调用" + di.getExp() + "的验证=======");

            dp_flag = dp_flag && di_flag;
        }

        errorDeal(dp_flag, Data.DemandPath, dp, rep_id);
        System.out.println("========结束：验证需求路径" + dp.getName() + "=======");

        return dp_flag;
    }

    private void errorDeal(boolean flag, String errClassify, Object o, String rep_id) {
        if (flag) {
            reportService.saveError(errClassify + "_1", o, rep_id);
        } else {
            reportService.saveError(errClassify + "_2", o, rep_id);
        }
    }

    private void errorDeal(boolean flag, String errClassify, Object o, int title, String rep_id) {
        if (flag) {
            reportService.saveError(errClassify + "_1", o, title, rep_id);
        } else {
            reportService.saveError(errClassify + "_2", o, title, rep_id);
        }
    }

    /**
     * 类型结构验证：验证一个类型的正确性
     *
     * wrongTypeList
     * input: t // 待验证类型
     * output: flag
     *      判断f及其依赖类型是否正确，返回缺失和存在的类型列表
     */
    public boolean DEV_SR1(SamlType t, String rep_id) {
        System.out.println("----------开始：验证类型" + t.getName() + "的正确性----------");
        //List<SamlType> wrongTypeList = new ArrayList<>();
        //List<SamlType> rightTypeList = new ArrayList<>();
        boolean t_flag = false;
        if (ruleExistService.TR01(t, rep_id)) {
            t_flag = true;
            errorDeal(t_flag, Data.Base, t, rep_id);
        } else if (ruleExistService.TR02(t, rep_id)) {
            t_flag = ruleStructService.TR_func(t, rep_id);
            errorDeal(t_flag, Data.Func, t, rep_id);
        } else if (ruleExistService.TR03(t, rep_id)) {
            t_flag = ruleStructService.TR_struct(t, rep_id);
            errorDeal(t_flag, Data.Struct, t, rep_id);
        } else if (ruleExistService.TR04(t, rep_id)) {
            t_flag = ruleStructService.TR_file(t, rep_id);
            errorDeal(t_flag, Data.File, t, rep_id);
        } else if (ruleExistService.TR05(t, rep_id)) {
            t_flag = ruleStructService.TR_dentry(t, rep_id);
            errorDeal(t_flag, Data.Dentry, t, rep_id);
        } else if (ruleExistService.TR06(t, rep_id)) {
            t_flag = ruleStructService.TR_subsys(t, rep_id);
            errorDeal(t_flag, Data.Subsys, t, rep_id);
        } else {

        }

        System.out.println("----------结束：验证类型" + t.getName() + "的正确性----------");

        return t_flag;
    }

    @Value("${veri.openValid}")
    private boolean openValid;

    /**
     * 仅仅针对函数调用的那两个，别的函数不行。
     * 因为Title不同！这里是FuncTitle，其他DEV_SR是TypeTitle。
     */
    private boolean DEV_SR_2Func(SamlType f, String rep_id) {
        System.out.println("----------开始:验证函数" + f.getName() + "的正确性----------");
        int vali = f.getValid();
        if (!openValid) { // 关掉状态判断，要不然报告就有问题了。
            vali = 2;
        }
        if (vali == Data.Valid0) {
            System.out.println("----------【已验】结束:验证函数" + f.getName() + "的正确性:false----------");
            return false;
        } else if (vali == Data.Valid1) {
            System.out.println("----------【已验】结束:验证函数" + f.getName() + "的正确性:true----------");
            return true;
        } else { // 未验证过
            int level = f.getLevel();
            boolean f_flag;
            if (level == Data.FUNC) {
                f_flag = ruleStructService.TR_func(f, rep_id);
                errorDeal(f_flag, Data.Func, f, Data.FuncTitle, rep_id);
            } else {
                System.out.println("此方法不能用！");
                f_flag = false;
            }

            vali = f_flag ? 1 : 0;
            samlTypeDao.updateValid(f.getId(), vali);

            System.out.println("----------结束:验证函数" + f.getName() + "的正确性:" + f_flag + "----------");
            return f_flag;
        }
    }

    @Override
    public boolean DEV_SR(SamlType t, String rep_id) {
        System.out.println("----------开始:验证类型" + t.getName() + "的正确性----------");
        int vali = t.getValid();
        if (!openValid) { // 关掉状态判断，要不然报告就有问题了。
            vali = Data.Valid2;
        }
        if (vali == Data.Valid0) {
            System.out.println("----------【已验】结束:验证类型" + t.getName() + "的正确性:false----------");
            return false;
        } else if (vali == Data.Valid1) {
            System.out.println("----------【已验】结束:验证类型" + t.getName() + "的正确性:true----------");
            return true;
        } else {
            int level = t.getLevel();
            boolean t_flag = true;
            String exType = "";
            String rightType = "";
            boolean exist = samlTypeDao.existsById(t.getId());
            if (exist) {
                if (level == Data.BASE) {
                    exType = Data.BaseExist;
                    rightType = Data.Base;
                    t_flag = true;
                } else if (level == Data.FUNC) {
                    exType = Data.FuncExist;
                    rightType = Data.Func;
                    t_flag = ruleStructService.TR_func(t, rep_id);
                } else if (level == Data.STRUCT) {
                    exType = Data.StructExist;
                    rightType = Data.Struct;
                    t_flag = ruleStructService.TR_struct(t, rep_id);
                } else if (level == Data.FILE) {
                    exType = Data.FileExist;
                    rightType = Data.File;
                    t_flag = ruleStructService.TR_file(t, rep_id);
                } else if (level == Data.DENTRY) {
                    exType = Data.DentryExist;
                    rightType = Data.Dentry;
                    t_flag = ruleStructService.TR_dentry(t, rep_id);
                } else if (level == Data.SUBSYS) {
                    exType = Data.SubsysExist;
                    rightType = Data.Subsys;
                    t_flag = ruleStructService.TR_subsys(t, rep_id);
                } else if (level == Data.ENUM || level == Data.UNION) {
                    exType = Data.EnumExist;
                    rightType = Data.Enum;
                    t_flag = true;
                } else {
                    exType = Data.NotLevelTypeExist;
                    t_flag = false;
                }
            } else {
                exType = Data.LackError;
                t_flag = false;
            }
            reportService.saveError(exType, t, Data.TypeExTitle, rep_id);
            if (!rightType.equals("")) {
                errorDeal(t_flag, rightType, t, Data.TypeTitle, rep_id);
            }

            vali = t_flag ? 1 : 0;
            samlTypeDao.updateValid(t.getId(), vali);

            System.out.println("----------结束:验证类型" + t.getName() + "的正确性:" + t_flag + "----------");
            return t_flag;
        }
    }

    /**
     * 函数调用关系验证：验证一个函数调用的正确性
     *
     * input: d //待验证调用关系
     * output: flag
     *
     */
    @Override
    public boolean DEV_IR(DemandInvoke di, String rep_id) {
        System.out.println("----------开始：验证函数调用关系：" + di.getExp() + "----------");
        int vali = di.getValidIr();
        if (!openValid) { // 关掉状态判断，要不然报告就有问题了。
            vali = Data.Valid2;
        }
        if (vali == Data.Valid0) {
            System.out.println("----------【已验】结束:验证函数调用关系" + di.getName() + "的正确性:false----------");
            return false;
        } else if (vali == Data.Valid1) {
            System.out.println("----------【已验】结束:验证函数调用关系" + di.getName() + "的正确性:true----------");
            return true;
        } else {
            //boolean flag = distributeTR(di);
            boolean flag = false;
            int classify = di.getRelationClassify();
            if (classify == Data.Invocable) {
                //flag = TR21(di, rep_id) || TR22(di, rep_id);  //后者没实现，算啦
                System.out.println("代入公式(TR24)：");
                flag = TR21(di, rep_id);

                System.out.println("公式(TR24)验证结果：" + flag);
            } else if (classify == Data.Accessible) {
                System.out.println("【根本不会出现！】");
                flag = TR23(di, rep_id) || TR24(di, rep_id);
            } else {
                System.out.println("【根本不会出现！】关系不对，不能代入公式：TR21，TR22，TR23，TR24。");
                errorDeal(false, Data.InvokeAccessError, di, rep_id);
                flag = false;
            }

            SamlRelation ir = new SamlRelation(di.getF1Exp() + "--≪Invokable≫-->" + di.getF2Exp());
            errorDeal(flag, Data.FuncInvoke, ir, Data.FuncInvTitle, rep_id);
            //errorDeal(flag, Data.FuncInvoke, ir, Data.RelaTitle, rep_id); // 还是不要了吧，降级太严重了，不同级别显不出来，算啦

            System.out.println("----------结束：验证函数调用关系：" + di.getExp() + "," + flag + "----------");

            vali = flag ? 1 : 0;
            demandInvokeDao.updateValidIr(di.getId(), vali);

            return flag;
        }

    }

    /**
     * 获取依赖类型, 包括递归的父类型、参数和返回值类型
     */
    private List<Integer> getDependTypeList(SamlType func) {
        List<Integer> fathers = samlTypeService.getFuncRecurFathers(func);
        //fathers = new ArrayList<>();
        List<Integer> paraAndReturn = samlTypeService.getFuncDependParaAndReturn(func);
        List<Integer> dependTypeList = samlTypeService.mergeNoRepeat(fathers, paraAndReturn);// 当前func的全部依赖类型，合并去重。
        return dependTypeList;
    }

    /**
     * 从函数调用中获取函数列表，也就是主调函数和被调函数。
     */
    private List<SamlType> getFuncList(DemandInvoke d, String rep_id) {
        int did = d.getId();

        String[] tmp = d.getExp().split(";");
        String f1_str = tmp[0];
        String f2_str = tmp[1];
        SamlType f1 = samlTypeDao.findSamlTypeById(d.getFunc1());
        SamlType f2 = samlTypeDao.findSamlTypeById(d.getFunc2());

        if (f1 == null || f1.getLevel() != Data.FUNC) {
            SamlType t = new SamlType(did + ":" + f1_str);
            String err = Data.FuncLackError + ":" + d.getExp() + "的主调函数不存在";
            System.out.println(err);
            reportService.saveError(Data.FuncLackError, t, rep_id);
            return null;
        }
        if (f2 == null || f2.getLevel() != Data.FUNC) {
            SamlType t = new SamlType(did + ":" + f2_str);
            String err = Data.FuncLackError + ":" + d.getExp() + "的被调函数不存在";
            System.out.println(err);
            reportService.saveError(Data.FuncLackError, t, rep_id);
            return null;
        }

        List<SamlType> funcList = new ArrayList<>();
        funcList.add(f1);
        funcList.add(f2);
        return funcList;
    }


    ///**
    // * 验证函数正确性。
    // * 同TR_func？？？不同
    // *      VERI_FUNC：是大函数正确性，针对所有依赖类型验证，包括其父类和子类。
    // *      TR_func：是小函数正确性，结构正确性。
    // *
    // * 怎么感觉，真的要套娃了呀？函数正确性依赖类型，类型正确性依赖函数？？
    // */
    //@Override
    //@Deprecated
    //public boolean VERI_FUNC(SamlType func, List<ErrorClassify> errList) {
    //    ////1、获取函数依赖类型列表，包括函数自身。错误打印。
    //    //List<SamlType> dependTypeList = getDependTypeList(func, errList);
    //    //
    //    ////2、再一个个检查类型结构！！！
    //    //System.out.println("========开始：验证函数" + func.getName() + "的依赖类型=======");
    //    ////List<SamlType> wrongTypeList = new ArrayList<>();
    //    //boolean flag = DEV_SR(dependTypeList);
    //    //if (flag) {
    //    //    System.out.println("验证结果：函数" + func.getName() + "的依赖类型均正确");
    //    //} else {
    //    //    System.out.println("验证结果：函数" + func.getName() + "的依赖类型有错误");
    //    //
    //    //}
    //    //System.out.println("========结束：验证函数" + func.getName() + "的依赖类型=======");
    //    //
    //    //return flag;
    //    return false;
    //}


    //@Override
    //@Deprecated
    //public void DEV_IR_ByRule(List<ErrorClassify> errList) {
    //    //函数无效就不考虑了，因为那是类型检测的任务。
    //    List<DemandInvoke> demands = demandInvokeDao.findByRelationClassifyAndValid(Data.Invocable, Data.Valid);
    //
    //    for (DemandInvoke cur : demands) {
    //        System.out.println("----------开始：检测调用关系：" + cur.getExp() + "----------");
    //        if (distributeTR(cur)) {
    //            System.out.println("结果：" + cur.getExp() + "可调用");
    //        } else {
    //            System.out.println("结果：" + cur.getExp() + "不可调用");
    //        }
    //        System.out.println("----------结束：检测调用关系：" + cur.getExp() + "----------");
    //    }
    //}


    ///**
    // *
    // * 输入是一个关系，输出是这个关系是否存在，true/false。
    // *
    // * 根据约束匹配规则，再根据结论，也就是传入的关系，分发到相应的条件：
    // * 一、f1和f2都是函数
    // *  1、传入调用关系f1->f2：我这是在验证调用关系是否成立。
    // *      (1)TR21：f1.scope <: f2.scope
    // *          (1.1)TR25：判断作用域小的情况
    // *      (2)
    // *          (2.1)f2是某个Ts的成员函数：TR31
    // *              (2.1.1)TR22：f1--<<access>>-->Ts
    // *
    // * 二、f1是函数, Ts是结构体
    // *  1、传入f1--<<access>>-->Ts：验证可访问关系是否成立。
    // *      (1)TR23：f1.scope <: Ts.scope
    // *          (1.1)TR25
    // *      (2)TR24：Ts是Ts'的成员属性 && f1--<<access>>-->Ts'【递归，直到Ts和Ts'没有VarAssociation关系终止】
    // *          (2.1)TR12_1：Ts是Ts'的成员属性，判断Ts--<<var>>-->Ts'关系是否存在。
    // *          (2.2)f1--<<access>>-->Ts'
    // *              TR23：f1.scope <: Ts'
    // *
    // *
    // * */
    //private boolean distributeTR(DemandInvoke di) {
    //    int classify = di.getRelationClassify();
    //    if (classify == Data.Invocable) {
    //        return TR21(di) || TR22(di);
    //    } else if (classify == Data.Accessible) {
    //        return TR23(di) || TR24(di);
    //    } else {
    //        System.out.println("关系不对，不能代入公式：TR21，TR22，TR23，TR24。");
    //        errorDeal(false, Data.InvokeAccessError, di);
    //        return false;
    //    }
    //}

    /**
     * 已知结论是，f1--<<access>>-->T2
     * 约束：Accessible && R33,f1是函数 && T2是结构体
     * 返回条件：TR25：f1.scope <: T2.scope
     */
    public boolean TR23(DemandInvoke samlDemand, String rep_id) {
        int classify = samlDemand.getRelationClassify();
        if (classify == Data.Invocable) { // 约束
            SamlType field1 = samlTypeDao.findSamlTypeById(samlDemand.getFunc1());
            SamlType field2 = samlTypeDao.findSamlTypeById(samlDemand.getFunc2());
            if (ruleExistService.TR02(field1, rep_id) && ruleExistService.TR03(field2, rep_id)) { //约束
                return TR25(field1, field2, rep_id);
            } else {
                System.out.println("TR23 Error:TR02(" + field1 + ")&&TR03(" + field2 + ")，约束不满足");
                return false;
            }
        } else {
            System.out.println("TR23 Error:" + classify + "==" + Data.Invocable + "，约束不满足");
            return false;
        }
    }

    /**
     * 已知结论是，f1--<<access>>-->T2
     * 约束：Accessible && R33,f1是函数 && T2是结构体 && T2=T2'.vars
     * 返回条件：f1--<<access>>-->T2'
     */
    public boolean TR24(DemandInvoke samlDemand, String rep_id) {
        int classify = samlDemand.getRelationClassify();
        if (classify == Data.Invocable) { // 约束
            SamlType field1 = samlTypeDao.findSamlTypeById(samlDemand.getFunc1());
            SamlType field2 = samlTypeDao.findSamlTypeById(samlDemand.getFunc2());

            List<SamlType> f2Father = TR12_1(field2); // 得到所有field2所在的结构体, 即成员属性类型是field2的结构体。
            if (ruleExistService.TR02(field1, rep_id) && ruleExistService.TR03(field2, rep_id) && f2Father != null) { //约束
                for (SamlType s : f2Father) {
                    DemandInvoke tmp = new DemandInvoke(Data.Accessible, field1.getId(), s.getId()); // 新的Accessible关系
                    //boolean flag = distributeTR(tmp);//重新分发
                    boolean flag = TR23(tmp, rep_id) || TR24(tmp, rep_id);
                    if (flag) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 已知结论是，f1->T2.f2
     * 约束：R33,f1是函数 && R33,f2是结构体T2的成员函数
     * 返回条件：f1--<<access>>-->T2
     */
    public boolean TR22(DemandInvoke samlDemand, String rep_id) {
        int classify = samlDemand.getRelationClassify();
        if (classify == Data.Accessible) { // 约束
            SamlType f1 = samlTypeDao.findSamlTypeById(samlDemand.getFunc1());
            SamlType f2 = samlTypeDao.findSamlTypeById(samlDemand.getFunc2());

            SamlType f2Father = TR31(f2);
            if (ruleExistService.TR02(f1, rep_id) && f2Father != null) { //约束
                DemandInvoke tmp = new DemandInvoke(Data.Accessible, f1.getId(), f2Father.getId()); // 新的Accessible关系
                //return distributeTR(tmp);//重新分发
                return TR23(tmp, rep_id) || TR24(tmp, rep_id);
            }
        }
        return false;
    }

    /**
     * 已知结论是，f1->f2
     * f1是全局
     * f2是全局时，f1的文件F1包含f2的声明，或者嵌套包含。
     *      1、F1=F2，OK
     *      2、F1的直接非static声明中，有f2，OK
     *      3、嵌套一层情况：所有包含了f2声明的文件，全部拿出来，看看f1的include里面有没有，如果有就OK
     *      4、多层嵌套情况：找到所有f1的include文件集合，包括嵌套的所有include文件！，看里面有没有和2交叉。如果有就OK。
     *
     * f2是成员时，不弄了，没有，算了。
     */
    @Override
    public boolean TR21(DemandInvoke samlDemand, String rep_id) {
        int classify = samlDemand.getRelationClassify();
        if (classify == Data.Invocable) {
            SamlType func1 = samlTypeDao.findSamlTypeById(samlDemand.getFunc1());
            SamlType func2 = samlTypeDao.findSamlTypeById(samlDemand.getFunc2());

            int id1 = func1.getId();
            int id2 = func2.getId();
            String file1 = func1.getPath();
            String file2 = func2.getPath();
            //1、F1=F2，OK
            if (file1.equals(file2)) return true;

            // 2、F1的直接非static声明中，有f2，OK
            List<IncludeImport> dire_declare = includeImportDao.findByLevelAndPathAndOriginType(Data.FUNC_DECLARE, file1, id2);
            if (dire_declare != null && !dire_declare.isEmpty()) {
                if (dire_declare.get(0).getStaticMark() == 0) { // 非static的，但可以是static inline
                    return true;
                }
            }

            // 2、直接包含了定义所在的文件，如果是.c就不可能啊。
            if (file2.endsWith(".h")) {
                List<IncludeImport> dire_inc = includeImportDao.findByLevelAndPathAndExp(Data.FUNC_DECLARE, file1, file2);
                if (dire_inc != null && !dire_inc.isEmpty()) {
                    return true;
                }
            }

            // 3、只要以下两个list有交叉就好！
            // 3.1 找到所有声明了f2的.h文件集合。
            List<String> f2_dec_file = includeImportDao.findHFiles(Data.FUNC_DECLARE, id2); //所有path
            System.out.println("=================声明了" + func2.getName() + "的所有文件：======================");
            System.out.println(f2_dec_file);

            // 3.2 找到所有f1的include文件集合，包括嵌套的所有include文件！
            List<String> f1_all_inc = new ArrayList<>();
            int f1_id = func1.getFather();
            IncludeResult exist = includeResultDao.findByFileId(f1_id);
            if (exist == null) {
                List<String> f1_dire_inc = includeImportDao.findByPath(file1);
                f1_all_inc = revInc(f1_all_inc, f1_dire_inc);
                String str = f1_all_inc.toString().replaceAll("\\[|\\]| ", "");
                str = StrUtil.distinct(str);//include嵌套文件去重
                includeResultDao.insertIncludeList(f1_id, str);
            } else {
                String[] strs = exist.getIncludeFileList().split(",");
                for (String s : strs) {
                    f1_all_inc.add(s);
                }
            }
            System.out.println("=================" + func1.getName() + "的include文件集合：======================");
            System.out.println(f1_all_inc);

            for (String f : f2_dec_file) {
                if (f1_all_inc.contains(f)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 一开始，全是待处理的newl，oldl为空。
     */
    private List<String> revInc(List<String> oldl, List<String> newl) {
        if (newl == null || newl.isEmpty()) {
            return oldl;
        }
        oldl.addAll(newl); // 先把所有的new加到old里面
        List<String> newl2 = new ArrayList<>();
        for (String i : newl) { // includeId
            List<String> tmp = includeImportDao.getIncByExp(i);
            if (tmp != null && !tmp.isEmpty()) {
                for (String j : tmp) { // 判断，不重复再加入
                    if (!oldl.contains(j)) {
                        newl2.add(j);
                    }
                }
            }
        }
        return revInc(oldl, newl2);
    }

    /**
     * 已知结论是，f1->f2
     * 已知条件二：R33, f1是函数 && R33, f2是函数
     * 返回条件一：TR25, f1.scope <: f2.scope
     */
    @Deprecated
    public boolean TR211(DemandInvoke samlDemand, String rep_id) {
        int classify = samlDemand.getRelationClassify();
        if (classify == Data.Invocable) {
            SamlType func1 = samlTypeDao.findSamlTypeById(samlDemand.getFunc1());
            SamlType func2 = samlTypeDao.findSamlTypeById(samlDemand.getFunc2());
            if (ruleExistService.TR02(func1, rep_id) && ruleExistService.TR02(func2, rep_id)) {
                return TR25(func1, func2, rep_id);
            } else {
                String err = Data.TR1_Error1 + ":" + func1.getName() + "," + func2.getName() + "都是函数不成立。————TR21";
                //errList.add(new ErrorClassify(Data.TR1_Error1, err));
                System.out.println(err);

                return false;
            }
        } else {
            String err = Data.TR1_Error + ":" + samlDemand.getExp() + "不是调用关系，不能代入公式TR1。";
            //errList.add(new ErrorClassify(Data.TR1_Error, err));
            System.out.println(err);

            return false;
        }
    }

    /**
     * 判断 f1.scope <: f2.scope
     *
     * (1)约束：f1是函数或结构体 && TR03, F2=f2.scope是结构体
     *      F2是结构体 <==> f2是成员函数
     *      故：
     *          f1是函数或结构体 && TR31, f2是成员函数
     * 返回条件：f1.scope == f2.scope
     *
     * (2)约束：f1是函数或结构体 && TR35，F2=f2.scope是文件
     *      F2=f2.scope是文件 <==> f2是全局函数 || f2是结构体
     * 返回条件：f1.scope == f2.scope || f1.scope == (f2.scope).structs
     *
     *
     * ((s_1=s_2,with<f_1,f_2 都是成员函数>)
     * or (s_2=s_1  or s_2.h∈s_1.include or f_2∈s_1.extern ,with<f_1,f_2 都是全局函数>)
     * or (s_2=s_1.fahter or s_2.h∈s_1.fahter.include or f_2∈s1.fahter.extern ,with<f_1 是成员函数,f_2 是全局函数>))
     * ——————————————————————————————————————————————————————————————————————————————————————————————————————————————
     *    (s_1=f_1.scope≤ s_2=f_2.scope)(TR25)
     *
     */
    private boolean TR25(SamlType f1, SamlType f2, String rep_id) {

        SamlType s1 = samlTypeDao.findSamlTypeById(f1.getFather());

        List<SamlType> s2 = new ArrayList<>();
        s2.add(samlTypeDao.findSamlTypeById(f2.getFather()));

        //判断s1.include是不是包含s2


        boolean f1Flag = ruleExistService.TR02(f1, rep_id) || ruleExistService.TR03(f1, rep_id);
        SamlType f2Father = samlTypeDao.findSamlTypeById(f2.getFather());
        SamlType f1Father = samlTypeDao.findSamlTypeById(f1.getFather());
        if (f1Flag && TR31(f2) != null) {//约束1
            return f2Father.getId() == f1Father.getId();
        } else if (f1Flag && (TR32(f2) != null || ruleExistService.TR03(f2, rep_id))) {//约束2
            return f2Father.getId() == f1Father.getId() ||
                    f1Father.getFather() == f2Father.getId();
        }
        return false;
    }

    /**
     * 已知结论是，samlType是成员函数。
     * 给出 结论，返回条件
     * 由于条件是【终结符】，直接判断，如果是返回该结构体，如果不是返回null。
     */
    public SamlType TR31(SamlType samlType) {
        return typeAndFatherlevel(samlType, 3);
    }

    /**
     * 已知结论是，f2是全局函数。
     * 给出 结论，返回条件
     * 由于条件是【终结符】，直接判断，如果是返回该文件，如果不是返回null。
     */
    public SamlType TR32(SamlType samlType) {
        return typeAndFatherlevel(samlType, 4);
    }


    /**
     * 已知成员Ts, 求与其有VarAssociation关系的父结构体。
     * 返回：可能的结构体列表。
     *
     *
     * 判断Ts是某个结构体Ts'的成员变量， Ts = Ts'.vars
     *
     * 方法1：查找VarAssociation关系是否存在。
     *
     * 方法2：按照一般逻辑，
     *      (1)Ts和Ts'都存在
     *      (2)Ts是结构体
     *      (3)Ts'是Ts.field1的成员
     * */
    public List<SamlType> generateTypeAssociation(SamlType samlType) {
        return null;
    }

    /**
     * 推理出成员关系
     *
     */
    public List<SamlType> TR12_1(SamlType samlType) {
        //方法1：
        //后面的更改：开始改saml_relation表为1-1关系。

        //方法2：

        return null;
    }

    /**
     * 根据父亲的层次，判断samlType是成员函数还是全局函数。
     */
    private SamlType typeAndFatherlevel(SamlType samlType, int level) {
        if (samlType.getLevel() == 2) {
            SamlType father = samlTypeDao.findSamlTypeById(samlType.getFather());
            if (father != null && father.getLevel() == level) {
                return father;
            }
        }
        return null;
    }

}
