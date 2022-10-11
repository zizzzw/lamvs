package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.BaseTypeDao;
import com.dynamic_validate.dao.SamlListDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.SamlList;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleStructServiceImp implements RuleStructService {

    @Autowired
    private BaseTypeDao baseTypeDao;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private ReportService reportService;
    @Autowired
    private SamlTypeService samlTypeService;
    @Autowired
    private RuleExistService res;
    @Autowired
    private RuleRelationService rrs;
    @Autowired
    private SamlListDao samlListDao;

    @Override
    public boolean TR_dentry(SamlType samlType, String rep_id) {
        return true;
    }

    @Override
    public boolean TR_subsys(SamlType samlType, String rep_id) {
        return true;
    }

    /**
     * 目录层及以上，都是自动生成的，不会出错！就不用验了。
     *
     * 文件还是要验证的，因为结构体或函数可能不能用。
     * 【但是，文件中只有涉及使用时才需要验证，否则只是出现声明和定义，无关紧要。】
     *
     * 【就算是声明了没用，
     * 声明了一个没有的东西没用到，
     * include了一个没用的东西，
     * 都是不会报错吧？】
     *
     * 另外，你的所谓验证也只是对father和成员类型验证，
     * 那都是自动生成的，真的没什么好验证的！
     *
     */
    @Override
    public boolean TR_file(SamlType t, String rep_id) {
        //System.out.println("代入公式(P4)：");
        //boolean flag = TR_file_1(t, rep_id) && TR_file_2(t, rep_id);
        //System.out.println("公式(P4)验证结果：" + flag);
        return true;
    }

    /**
     * 文件父类型是目录或子系统，且其与父类型之间存在Agg关系。
     *
     * T=s.father,with
     * <
     *         TR_04(s)
     *      ∧
     *      (  TR_05 (T)  ∨  TR_06 (T)   )
     *      ∧ TR_14(s,T)
     * >
     * (P4-1)
     */
    @Override
    public boolean TR_file_1(SamlType t, String rep_id) {

        return false;
    }

    /**
     * l1=s.list1=[Ti]*,l2= s.list2=[Tj]*,with
     * 〈
     *      (l1!=null  ∧  TR_04(T_i)  ∧  TR_14(T_i,s)   )
     *      ∨
     *      (l2!=null  ∧  TR_05(T_j)  ∧   TR_14(T_j,s)    )
     * 〉
     * (P4-2)
     */
    @Override
    public boolean TR_file_2(SamlType t, String rep_id) {
        return false;
    }

    @Override
    public boolean TR_struct(SamlType t, String rep_id) {
        System.out.println("代入公式(P3)：");
        boolean flag = TR_struct_1(t, rep_id) && TR_struct_2(t, rep_id);
        System.out.println("公式(P3)验证结果：" + flag);
        return flag;
    }

    /**
     * 结构体父类型是文件，且其与父类型之间存在StructFile关系。
     *
     * T=s.father,with
     * <
     *      TR_03(s) ∧ TR_04(T) ∧ TR_13_2(s,T)
     * >
     * (P3-1)
     */
    @Override
    public boolean TR_struct_1(SamlType s, String rep_id) {
        int tid = s.getFather();
        SamlType t;
        if (tid == 0) {
            return false;
        } else {
            t = samlTypeDao.findById(tid).get();

            return res.TR03(s, rep_id)
                    && res.TR04(t, rep_id)
                    && rrs.TR13_2(s, t, rep_id);
        }
    }

    /**
     * l1=s.list1=[Ti]*,l2= s.list2=[Tj]*,with
     * 〈
     *      (l1!=null  ∧  (    TR_01(T_i) ∨ TR_03(T_i)   )  ∧  TR_12_1(T_i,s)   )
     *      ∨
     *      (l2!=null  ∧  TR_02(T_j)  ∧   TR_12_2(T_j,s)    )
     * 〉
     * (P3-2)
     *
     * 2022年5月24日 18:34:07，修改： l1加个TR_02(T_i)，因为有函数别名的情况，是可以使用的！
     * l1=s.list1=[Ti]*,l2= s.list2=[Tj]*,with
     * 〈
     *      (l1!=null  ∧  (    TR_01(T_i) ∨ TR_02(T_i)  ∨ TR_03(T_i)   )  ∧  TR_12_1(T_i,s)   )
     *      ∨
     *      (l2!=null  ∧  TR_02(T_j)  ∧   TR_12_2(T_j,s)    )
     * 〉
     * (P3-2)
     */
    @Override
    public boolean TR_struct_2(SamlType s, String rep_id) {
        int sid = s.getId();
        int l1id = s.getList1();
        int l2id = s.getList2();
        boolean flag1 = true, flag2 = true;
        if (l1id != 0) {
            SamlList l1 = samlListDao.findById(l1id).get();
            String[] ids = l1.getMemberType().split(",");
            String[] es = l1.getExp().split(",");

            for (int i = 0; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                if (id == 0) {
                    System.out.println("结构体" + sid + "成员类型" + es[i] + "不存在!");
                    SamlType t = new SamlType(sid + ":" + es[i]);
                    reportService.saveError(Data.LackError, t, Data.TypeTitle, rep_id);
                    flag1 = false;
                } else {
                    SamlType t = new SamlType(es[i]);
                    System.out.println("结构体" + sid + "成员类型" + es[i] + "存在!");
                    String errType;
                    if (res.TR01(id, rep_id)) {
                        errType = Data.BaseExist;
                    } else if (res.TR02(id, rep_id)) {
                        errType = Data.FuncExist;
                    } else if (res.TR03(id, rep_id)) {
                        errType = Data.StructExist;
                    } else { //参数类型层次不对
                        errType = Data.VarLevelError;
                        flag1 = false;
                    }
                    reportService.saveError(errType, t, rep_id);

                    flag1 = flag1 && rrs.TR12_1(id, s, rep_id); // 判断关系是否正确
                }
            }
        } else {
            flag1 = false;
        }

        if (l2id != 0) {
            SamlList l2 = samlListDao.findById(l2id).get(); //果然id弄反了！！
            String[] ids = l2.getMemberType().split(",");
            String[] es = l2.getExp().split(",");

            for (int i = 0; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                if (id == 0) {
                    System.out.println("结构体" + sid + "成员函数" + es[i] + "不存在!");
                    SamlType t = new SamlType(sid + ":" + es[i]);
                    //reportService.saveError(Data.LackError, t, rep_id);
                    reportService.saveError(Data.LackError, t, Data.TypeTitle, rep_id);
                    flag2 = false;
                } else {
                    SamlType t = new SamlType(es[i]);
                    System.out.println("结构体" + sid + "成员函数" + es[i] + "存在!");
                    String errType;
                    if (res.TR02(id, rep_id)) {
                        errType = Data.FuncExist;
                    } else { //成员函数层次不对
                        errType = Data.MethodLevelError;
                        flag2 = false;
                    }
                    reportService.saveError(errType, t, rep_id);

                    flag2 = flag2 && rrs.TR12_2(id, s, rep_id); // 判断关系是否正确
                }
            }
        } else {
            flag2 = false;
        }

        return flag1 || flag2;
    }

    /**
     * 判断func类型是否正确，第一条性质
     */
    @Override
    public boolean TR_func(SamlType t, String rep_id) {
        System.out.println("代入公式(P2)：");
        boolean flag = TR_Func_1(t, rep_id) && TR_Func_2(t, rep_id);
        System.out.println("公式(P2)验证结果：" + flag);
        return flag;

    }

    /**
     * func性质1
     * ①father的level = struct/file = 3/4
     *      TR15_1，aggregation，必须存在
     *
     * 当T是结构体时, 函数f与T之间存在FuncStruct关系
     * 当T是文件时, 全局函数f与T之间存在FuncFile关系.
     *
     * T=f.father,with
     * 〈
     * TR_02(f)∧(
     *            ( TR_03(T)∧TR_12_2(f,T) ) ∨ ( TR_04(T)∧TR_13_1(f,T) )
     *           )
     * 〉
     * (P1-1)
     *
     * 下面这个过于复杂了，非要把List搞出来，实际上意义不大：
     * T=f.father,list_func= T.list1,with<
     * TR_02 (f)∧TR_07 (list_func )∧TR_(15-2) (f,list_func)
     * ∧(  (TR_03 (T)∧TR_(12-1) (list_func,T))
     *    ∨(TR_04 (T)∧TR_(13-1) (list_func,T))
     *   )> (P2_1)
     */
    private boolean TR_Func_11(SamlType f, String rep_id) {
        int tid = f.getFather();
        if (tid == 0) {
            System.out.println("父类型不存在，报错");
            return false;
        }
        SamlType t = samlTypeDao.findById(tid).get();
        if (t.getLevel() == Data.FILE) {
            return rrs.TR13_1(f.getId(), tid, rep_id);
        } else if (t.getLevel() == Data.STRUCT) {
            return rrs.TR12_2(f.getId(), t, rep_id);
        }

        return false;
    }

    private boolean TR_Func_1(SamlType f, String rep_id) {
        int t = f.getFather();
        return res.TR02(f, rep_id) &&
                ((res.TR03(t, rep_id) && rrs.TR12_2(f.getId(), t, rep_id)) ||
                        (res.TR04(t, rep_id) && rrs.TR13_1(f.getId(), t, rep_id))
                );
    }

    /**
     * func性质2
     * ②
     * list2不可为null, 返回空时为void类型
     *      TR11，Param_out，必须存在。只要Param_in的list存在就满足。
     * list1和list2的组合方式正确: list_compound=multi即8
     *      TR15_1(muti)，aggregation，只要list存在就满足
     *
     *
     * list_in=f.param_in=[Ti]*,Type_out=f.param_out=Tj,with
     * 〈
     *   (
     *     list_in==null
     *     ∨
     *     (  list_in !=null  ∧  TR_Func_2_1(list_in )  )
     *   )
     *   ∧
     *   (  Type_out !=null  ∧  TR_Func_2_2(Type_out )  )
     *
     *   〉
     * (P1-2)
     *
     *
     * (TR_Func-2)
     *
     * list_in=f.param_in=[Ti]*,Type_out=f.param_out=T,
     * with〈 (list_in==null
     *       ∨(list_in !=null  ∧  TR_(Func-2-1)(list_in)
     *         )
     *       )
     *    ∧ (Type_out !=null  ∧  TR_(Func-2-2)(Type_out)
     *       )
     *    〉
     *
     */
    private boolean TR_Func_2(SamlType f, String rep_id) {
        boolean flag1, flag2;

        if (f.getList1() == 0) {
            flag1 = true;
            System.out.println("参数表为空，没问题，正好不用验了");
        } else {
            SamlList list_in = samlListDao.findSamlListById(f.getList1());
            flag1 = TR_Func_2_1(f.getId(), list_in, rep_id);
        }

        if (f.getList2() != 0) {
            SamlType type_out = samlTypeDao.findSamlTypeById(f.getList2());
            flag2 = TR_Func_2_2(f, type_out, rep_id);
        } else {
            flag2 = false;
            System.out.println("返回值不能为空！报错");
            reportService.saveError(Data.FuncReturnLackError, f, rep_id);
        }
        return flag1 && flag2;
    }

    private boolean TR_Func_21(SamlType f, String rep_id) {
        SamlList list_in = samlListDao.findSamlListById(f.getList1());
        boolean flag = true;
        if (list_in == null) {
            System.out.println("参数表为空，没问题，正好不用验了");
            SamlType type_out = samlTypeDao.findSamlTypeById(f.getList2());
            if (type_out == null) {
                flag = false;
                System.out.println("返回值不能为空！报错");
                reportService.saveError(Data.FuncReturnLackError, f, rep_id);
            } else {
                flag = TR_Func_2_2(f, type_out, rep_id);
            }
        } else {
            flag = TR_Func_2_1(f.getId(), list_in, rep_id);
        }
        return flag;
    }

    /**
     * 【没弄List递归】
     *
     * 【实现：2022年5月8日 02:14:35】
     * list_in=f.param_in=[Ti]*,with
     * 〈
     *      TR_11_1(Ti,f)
     *      ∧
     *      (
     *          TR_01(Ti)  ∨   TR_02(Ti)  ∨   TR_03(Ti)
     *      )
     * 〉
     * (P1-2-1)
     *
     *
     *
     * (TR_Func-2-1)
     *
     * list_in=f.param_in=[Ti]*,
     * with〈  TR_(11-1)(Ti,f)
     *     ∧(  TR_01 (Ti)∨TR_03 (Ti)
     *       ∨ (TR_07 (Ti)∧TR_L1(Ti))
     *       )
     *     〉
     *
     * 讲真，这个TR11-1没一点屁用，因为就是从这里获取的，还有什么好判断的？
     *
     * 实际情况，param_in就认为都是层次类型, 没有List得了！所以(TR_07 (Ti)∧TR_L1(Ti))也不用实现了。。。
     * 【不是懒，主要是List和Type类型不通, 真的有些艰难啊，虽然可以考虑弄个父类或接口，但感觉意义不大，算了，先弄出结果再说吧。】
     */
    private boolean TR_Func_2_1(int f, SamlList list, String rep_id) {
        boolean flag = true;
        String m_t = list.getMemberType();
        String e_t = list.getExp();

        String[] ids = m_t.split(",");
        String[] es = e_t.split(",");

        for (int i = 0; i < ids.length; i++) {
            int id = Integer.parseInt(ids[i]);
            if (id == 0) {
                System.out.println(f + "输入参数类型" + es[i] + "不存在!");
                SamlType t = new SamlType(f + ":" + es[i]);
                reportService.saveError(Data.FuncParamLackError, t, rep_id);
                flag = false;
            } else {
                SamlType t = new SamlType(es[i]);
                System.out.println("输入参数类型" + es[i] + "存在!");
                String errType;
                if (res.TR01(id, rep_id)) {
                    errType = Data.BaseExist;
                } else if (res.TR02(id, rep_id)) {
                    errType = Data.FuncExist;
                } else if (res.TR03(id, rep_id)) {
                    errType = Data.StructExist;
                } else { //参数类型层次不对
                    errType = Data.ParamLevelError;
                    flag = false;
                }
                reportService.saveError(errType, t, rep_id);

                flag = flag && rrs.TR11_1(id, f, rep_id); // 判断关系是否正确

                //flag = flag && rrs.TR23_1(id, f, rep_id) &&
                //        (res.TR01(id, rep_id) || res.TR03(id, rep_id)); // 这个时候按理说有一个对的就行，不该报错，但是，每一个TR0X都会报错，这该咋整？
            }
        }
        return flag;
    }

    /**
     * 老的：
     *   TR_(11-2)(T_i,f)
     * ∧(  TR_07 (list_out)
     *    ∧list_out.compound=multi
     *    ∧(  TR_01 (T_i) ∨ TR_03 (T_i)  )
     *   )  (TR-f-2-2)【公式形式】
     *
     *
     * 【没弄List递归】
     * 2022年4月13日17:29:26，更新
     * (TR_Func-2-2)：
     *   Type_out=f.param_out=T,
     *   with〈   TR_(11-2)(T,f)
     *       ∧(  TR_01 (T)∨TR_03 (T)
     *          ∨(  TR_07 (T)∧TR_L(T) )
     *         )
     *      〉
     *
     * 【实现：2022年5月8日 02:17:16】
     *
     * Type_out=f.param_out=Tj,with
     * 〈
     *    TR_11_2(Tj,f)
     *    ∧
     *    (   TR_01 (Tj)  ∨   TR_02 (Tj)  ∨   TR_03 (Tj)   )
     * 〉
     * (P1-2-2)
     */
    private boolean TR_Func_2_2(SamlType f, SamlType t, String rep_id) {
        boolean flag = true;
        if (t == null) {
            System.out.println("返回值类型" + t.getExp() + "不存在!");
            reportService.saveError(Data.FuncReturnLackError, t, rep_id);
            flag = false;
        } else {
            System.out.println("返回值类型" + t.getExp() + "存在!");
            String errType;
            if (res.TR01(t, rep_id)) {
                errType = Data.BaseExist;
            } else if (res.TR02(t, rep_id)) {
                errType = Data.FuncExist;
            } else if (res.TR03(t, rep_id)) {
                errType = Data.StructExist;
            } else { //返回值类型层次不对
                errType = Data.ReturnLevelError;
                flag = false;
            }
            reportService.saveError(errType, t, rep_id);

            flag = flag && rrs.TR11_2(t, f, rep_id); // 判断关系是否正确
        }
        return flag;
    }

    private boolean TR_Func_2_21(SamlType f, SamlType t, String rep_id) {
        return rrs.TR11_2(t, f, rep_id) &&
                (res.TR01(t, rep_id) || res.TR03(t, rep_id));
    }


    /**
     * 【估计要改成L1或L2的性质，暂时不用，再说吧】
     *
     * func性质3
     * ③list1/list2的member_type和member_list和成员类型对应
     *      member_type的成员在type中可查.    TR01~TR03，只要member_type存在就满足
     *      member_list的成员在list中可查.    TR07，只要member_list存在就满足

     * func性质4
     * ④空不空，得提出一个单独的要求
     *      list_out必不空，list_in可以为空。
     *      member_type和member_list至少一个不空。
     *
     * TR-f-5
     * list_out必不空，list_in可以为空。
     *
     * ( list_in ==null || ( list_in !=null && TR-f-2-1 && TR-f-4(list_in) )  )
     * && ( list_out !=null && TR-f-2-2 && TR-f-4(list_out))
     */
    @Deprecated
    private boolean TR_f_5(SamlType t) {
        boolean flag = false;
        int in_id = t.getList1();
        if (in_id == 0) { // list_in ==null
            flag = true;
        } else {
            SamlList list_in = samlListDao.findSamlListById(in_id);
            //flag = TR_Func_2_1(t) && TR_f_4(list_in);

        }
        return flag;
    }

}
