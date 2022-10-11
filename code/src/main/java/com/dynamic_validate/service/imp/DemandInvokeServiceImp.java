package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.DemandInvokeDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.dao.TypeLackDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.DemandInvoke;
import com.dynamic_validate.entity.ErrorClassify;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.entity.TypeLack;
import com.dynamic_validate.service.DemandInvokeService;
import com.dynamic_validate.service.ReportService;
import com.dynamic_validate.service.SamlTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DemandInvokeServiceImp implements DemandInvokeService {
    @Autowired
    private DemandInvokeDao demandInvokeDao;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private SamlTypeService samlTypeService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private TypeLackDao typeLackDao;

    @Override
    public void finishDemandFuncs() {
        List<DemandInvoke> list = demandInvokeDao.findAll();
        for (DemandInvoke d : list) {
            int did = d.getId();
            String invokable_relation = d.getExp();
            System.out.println(d.getId());
            int[] funcIds = samlTypeService.getFuncIds(invokable_relation);
            String[] funcs = invokable_relation.split(";");
            String str = "";
            if (funcIds[0] == 0) {
                String name = funcs[0];
                str = "主调函数" + name + "不存在";
                String cl = Data.FuncLackError;
                List<TypeLack> exist = typeLackDao.findByNameAndErrClassifyAndFatherId(name, cl, did);
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), "SamlType", did, name, str, cl);
                } else {
                    typeLackDao.insert("SamlType", did, name, str, cl);
                }

            } else {
                str = "主调函数" + funcs[0] + "存在，Id为：" + funcIds[0];
            }
            System.out.println(str);
            if (funcIds[1] == 0) {
                String name = funcs[1];
                str = "被调函数" + name + "不存在";
                String cl = Data.FuncLackError;
                List<TypeLack> exist = typeLackDao.findByNameAndErrClassifyAndFatherId(name, cl, did);
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), "SamlType", did, name, str, cl);
                } else {
                    typeLackDao.insert("SamlType", did, name, str, cl);
                }

            } else {
                str = "被调函数" + funcs[1] + "存在，Id为：" + funcIds[1];
            }
            System.out.println(str);
            demandInvokeDao.updateFuncs(d.getId(), funcIds[0], funcs[0], funcIds[1], funcs[1]);
        }
    }

    /**
     * 功能：
     *      （1）根据exp，完善demand_path表中的func1和func2字段，
     *      （2）并返回存在的需求函数Id列表。
     *
     *      打印：错误列表errorList
     *      返回：type表中存在的函数Id列表。
     */
    @Override
    public List<Integer> getAndfinishExistFuncs(List<ErrorClassify> errList) {
        List<Integer> existFuncList = new ArrayList<>();

        List<DemandInvoke> list = demandInvokeDao.findByRelationClassify(Data.Invocable);
        for (DemandInvoke d : list) {
            String invokable_relation = d.getExp();
            int[] funcIds = samlTypeService.getFuncIds(invokable_relation);
            String[] funcs = invokable_relation.split(";");

            for (int i = 0; i < funcIds.length; i++) {
                if (funcIds[i] == 0) {
                    String str = "函数" + funcs[i] + "不存在";
                    ErrorClassify err = new ErrorClassify(Data.FuncLackError, str);
                    errList.add(err);

                } else {
                    if (!existFuncList.contains(funcIds[i])) {
                        existFuncList.add(funcIds[i]);
                    }

                    String str = "函数" + funcs[i] + "存在，Id为：" + funcIds[i];
                    ErrorClassify err = new ErrorClassify(Data.OK, str);
                    errList.add(err);
                }
            }
            demandInvokeDao.updateFuncs(d.getId(), funcIds[0], funcs[0], funcIds[1], funcs[1]);
        }

        return existFuncList;
    }

    /**
     * 返回一个调用的两个函数, 如果不存在，就报错。
     * */
    @Override
    @Deprecated
    public List<SamlType> getFuncsByDemandInvoke(DemandInvoke d, List<ErrorClassify> errList) {
        List<SamlType> funcs = new ArrayList<>();
        SamlType f1 = samlTypeDao.findSamlTypeById(d.getFunc1());
        SamlType f2 = samlTypeDao.findSamlTypeById(d.getFunc2());
        if (f1 == null || f1.getLevel() != Data.FUNC) {
            String err = Data.FuncLackError + ":" + d.getExp() + "的主调函数不存在";
            System.out.println(err);
            //errList.add(new ErrorClassify(Data.FuncLackError, err));
        } else {
            funcs.add(f1);
        }
        if (f2 == null || f2.getLevel() != Data.FUNC) {
            String err = Data.FuncLackError + ":" + d.getExp() + "的被调函数不存在";
            System.out.println(err);
            //errList.add(new ErrorClassify(Data.FuncLackError, err));
        } else {
            funcs.add(f2);
        }
        return funcs;
    }

    @Override
    public void demandDistinct() {
        List<String> list = demandInvokeDao.findDistinctIds();
        for (String idstr : list) {
            String[] ids = idstr.split(",");
            int id1 = Integer.parseInt(ids[0]);
            DemandInvoke o1 = demandInvokeDao.findById(id1);
            String o1_exp = o1.getExp();
            for (int i = 1; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                DemandInvoke oi = demandInvokeDao.findById(id);
                String oi_exp = oi.getExp();
                if (oi_exp.equals(o1_exp) || o1_exp.equals(oi_exp + ";") || oi_exp.equals(o1_exp + ";")) {
                    demandInvokeDao.delete(oi);
                    //System.out.println("删掉重复：" + oi.getExp());
                } else {
                    System.out.println("exp竟然不一样，对比下看看：");
                    System.out.println(o1.getExp() + ",name:" + o1.getName());
                    System.out.println(oi.getExp() + ",name:" + oi.getName());
                }
            }
        }
    }


}
