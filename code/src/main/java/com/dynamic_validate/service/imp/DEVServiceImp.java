package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.DemandInvokeDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.DemandInvoke;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.DEVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DEVServiceImp implements DEVService {

    @Autowired
    private DemandInvokeDao demandInvokeDao;

    @Autowired
    private SamlTypeDao samlTypeDao;

    /**
     * 调用关系存在性验证
     *
     * 函数F1--invokable-->F2，分情况：
     * 1、S1.F1-->S2.F2
     *   S2被S1.F1使用,这个关系要存在。
     *      S2可被F1访问, F1--Accessible-->S2
     *          F1.Scope = f1/S1 <= S2.Scope={f2/S2, f2被include进的文件[fi]*}
     *              只需要看,f1<={f2,f2被include的文件},简化为判断(f1==f2 || f1 include了f2)
     *      S1.F1--DependAssociation--S2【X,越界了！】
     *          【这里是Reference,实际存在的东西,但是,我不知道函数具体代码,怎么判断Reference呢？根本获取不了啊！】
     *          我只能从参数、成员角度考虑，函数体内的Reference不在我的研究范围内！
     *
     * 扩大范围：F1-->S2.F2
     *      F1所在文件f1==f2 || f1 include了f2
     *
     * 2、F1-->f2.F2（F1所在文件为f1，甭管是全局还是结构体函数）
     *   f1 == f2 || f1 include了f2 || f1 extern了F2
     *
     */
    @Override
    public void DEV_IR() {
        List<DemandInvoke> demandInvocableList = demandInvokeDao.findAll();
        for (int i = 0; i < demandInvocableList.size(); i++) {
            DemandInvoke cur = demandInvocableList.get(i);
            System.out.println("----------检测调用关系：" + cur.getExp() + "----------");
            SamlType func1 = samlTypeDao.findSamlTypeById(cur.getFunc1());
            if (func1 == null) {
                System.out.println("主调函数id:" + cur.getFunc1() + " 不存在");
                System.out.println();
                continue;
            }
            SamlType file1 = findFuncFile(func1.getFather());
            SamlType func2 = samlTypeDao.findSamlTypeById(cur.getFunc2());
            if (func2 == null) {
                System.out.println("被调函数id:" + cur.getFunc2() + " 不存在");
                System.out.println();
                continue;
            }
            SamlType func2_father = samlTypeDao.findSamlTypeById(func2.getFather());
            if (func2_father.getLevel() == Data.FILE) {//F2是全局函数
                if (file1.getId() == func2_father.getId() || isInclude(file1, func2_father) || isExtern(file1, func2)) {
                    System.out.println(func1.getName() + "可调用" + func2.getName());
                } else {
                    System.out.println(func1.getName() + "不可调用" + func2.getName());
                }
            } else if (func2_father.getLevel() == Data.STRUCT) {//F2是结构体内函数
                SamlType file2 = findFuncFile(func2_father.getFather());
                if (file1.getId() == file2.getId() || isExtern(file1, func2)) {
                    System.out.println(func1.getName() + "可调用" + func2.getName());
                } else {
                    System.out.println(func1.getName() + "不可调用" + func2.getName());
                }
            }
            System.out.println();
        }


    }

    /**
     * 【未完】
     * 判断文件file1中是否extern了某函数func2
     */
    private boolean isExtern(SamlType file1, SamlType func2) {
        return true;
    }

    /**
     * 【未完】
     * 判断文件file1中是否include了某文件file2
     */
    private boolean isInclude(SamlType file1, SamlType file2) {
        return true;
    }

    /**
     * 找到函数所在的文件
     */
    private SamlType findFuncFile(int father_id) {
        SamlType func1_father = samlTypeDao.findSamlTypeById(father_id);
        System.out.println("func1_father:" + func1_father.getId());
        if (func1_father.getLevel() == Data.FILE) {
            return func1_father;
        } else if (func1_father.getLevel() == Data.STRUCT) {
            return samlTypeDao.findSamlTypeById(func1_father.getFather());//返回父亲Struct的父亲，即File
        }
        return null;
    }

}
