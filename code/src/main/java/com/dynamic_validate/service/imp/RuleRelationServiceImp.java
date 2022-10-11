package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.BaseTypeDao;
import com.dynamic_validate.dao.SamlListDao;
import com.dynamic_validate.dao.SamlRelationDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.SamlList;
import com.dynamic_validate.entity.SamlRelation;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.ReportService;
import com.dynamic_validate.service.RuleRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleRelationServiceImp implements RuleRelationService {

    @Autowired
    private BaseTypeDao baseTypeDao;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private SamlRelationDao samlRelationDao;
    @Autowired
    private ReportService reportService;
    @Autowired
    private SamlListDao samlListDao;

    /**
     *
     * 判断聚合关系。
     *      如果T=T_1×〖…×T_i×…×T〗_n,1≤i≤n, 则称类型T_i和类型T满足聚合关系, 记为T_i □(→┴⋄ ) T.
     *
     * f是t的组成, f聚合成t。
     *
     */
    @Override
    public boolean TR15_1(SamlType t, SamlList l) {

        return false;
    }

    @Override
    public boolean TR15_2(SamlType t, SamlList l) {

        return false;
    }

    /**
     * 重写，判断name之间是否有ParamIn关系！
     */
    @Override
    public boolean TR23_1(String t, String f, String rep_id, int pro) {
        SamlType t_o = samlTypeDao.findByNameAndProject(t, pro).get(0); // 重名的情况不好处理啊，唉，先按不重名得了。
        SamlType f_o = samlTypeDao.findByNameAndProject(f, pro).get(0);

        List<SamlRelation> relation = samlRelationDao.findRelation(Data.ParamIn, f_o.getId(), t_o.getId());
        boolean flag = !relation.isEmpty();

        String exp = t_o.getName() + "--≪ParamIn≫-->" + f_o.getName();

        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.ParamInOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.ParamInError, r, Data.RelaTitle, rep_id);
        }
        return flag;
    }

    /**
     * 重写，判断id之间是否有ParamIn关系！
     */
    @Override
    public boolean TR23_1(int t, int f, String rep_id) {
        SamlType t_o = samlTypeDao.findSamlTypeById(t);
        SamlType f_o = samlTypeDao.findSamlTypeById(f);

        List<SamlRelation> relation = samlRelationDao.findRelation(Data.ParamIn, f, t);
        boolean flag = !relation.isEmpty();

        String exp = t_o.getName() + "--≪ParamIn≫-->" + f_o.getName();

        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.ParamInOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.ParamInError, r, Data.RelaTitle, rep_id);
        }
        return flag;
    }

    /**
     * 判断输入参数关系【type————f】
     */
    @Override
    public boolean TR23_1(SamlType t, SamlType f, String rep_id) {
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.ParamIn, f.getId(), t.getId());
        boolean flag = !relation.isEmpty();

        String exp = t.getName() + "--≪ParamIn≫-->" + f.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.ParamInOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.ParamInError, r, Data.RelaTitle, rep_id);
        }

        return flag;
    }

    @Override
    public boolean TR11_1(int tid, int f, String rep_id) {
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.ParamIn, f, tid);
        boolean flag = !relation.isEmpty();
        SamlType t = samlTypeDao.findById(tid).get();
        SamlType f_o = samlTypeDao.findById(f).get();
        String exp = t.getName() + "--≪ParamIn≫-->" + f_o.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.ParamInOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.ParamInError, r, Data.RelaTitle, rep_id);
        }

        return flag;
    }

    @Override
    public boolean TR11_1(int tid, SamlType f, String rep_id) {
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.ParamIn, f.getId(), tid);
        boolean flag = !relation.isEmpty();
        SamlType t = samlTypeDao.findById(tid).get();
        String exp = t.getName() + "--≪ParamIn≫-->" + f.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.ParamInOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.ParamInError, r, Data.RelaTitle, rep_id);
        }

        return flag;
    }

    /**
     * 判断ParamOut关系【type————f】
     */
    @Override
    public boolean TR11_2(int tid, SamlType f, String rep_id) {
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.ParamOut, f.getId(), tid);
        boolean flag = !relation.isEmpty();
        SamlType t = samlTypeDao.findById(tid).get();
        String exp = t.getName() + "--≪ParamOut≫-->" + f.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.ParamOutOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.ParamOutError, r, Data.RelaTitle, rep_id);
        }

        return flag;
    }

    @Override
    public boolean TR11_2(SamlType t, SamlType f, String rep_id) {
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.ParamOut, f.getId(), t.getId());
        boolean flag = !relation.isEmpty();

        String exp = t.getName() + "--≪ParamOut≫-->" + f.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.ParamOutOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.ParamOutError, r, Data.RelaTitle, rep_id);
        }

        return flag;
    }

    /**
     * 判断输入参数关系【list_in————f，不是type————f】
     *
     * list_in 存在
     * list_compound=multi=8
     *
     */
    @Deprecated
    public boolean TR23_1(SamlType f, String rep_id) {
        int list1Id = f.getList1();
        if (list1Id == 0) {
            return false;
        } else {
            SamlList list1 = samlListDao.findSamlListById(list1Id);
            if (list1 == null) {
                return false;
            } else {
                if (list1.getListCompound() == 8) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    @Override
    public boolean TR12_1(int tid, SamlType struct, String rep_id) {
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.VarStruct, struct.getId(), tid);
        boolean flag = !relation.isEmpty();

        SamlType t = samlTypeDao.findById(tid).get();
        String exp = t.getName() + "--≪VarStruct≫-->" + struct.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.VarStructOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.VarStructError, r, Data.RelaTitle, rep_id);
        }

        return flag;
    }

    @Override
    public boolean TR12_1(SamlType t, SamlType struct, String rep_id) {
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.VarStruct, struct.getId(), t.getId());
        boolean flag = !relation.isEmpty();

        String exp = t.getName() + "--≪VarStruct≫-->" + struct.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.VarStructOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.VarStructError, r, Data.RelaTitle, rep_id);
        }

        return flag;
    }

    @Override
    public boolean TR12_2(int f, int s, String rep_id) {
        SamlType f_o = samlTypeDao.findSamlTypeById(f);
        SamlType s_o = samlTypeDao.findSamlTypeById(s);
        boolean flag = false;
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.FuncStruct, s, f, Data.field2Type);
        flag = !relation.isEmpty();

        String exp = f_o.getName() + "--≪FuncStruct≫-->" + s_o.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.FuncStructOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.FuncStructError, r, Data.RelaTitle, rep_id);
        }
        return flag;
    }

    @Override
    public boolean TR12_2(int f, SamlType s, String rep_id) {
        SamlType f_o = samlTypeDao.findSamlTypeById(f);
        boolean flag = false;
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.FuncStruct, s.getId(), f, Data.field2Type);
        flag = !relation.isEmpty();

        String exp = f_o.getName() + "--≪FuncStruct≫-->" + s.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.FuncStructOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.FuncStructError, r, Data.RelaTitle, rep_id);
        }
        return flag;
    }

    @Override
    public boolean TR12_2(String f, String s, String rep_id, int pro) {
        SamlType f_o = samlTypeDao.findByNameAndProject(f, pro).get(0);
        SamlType s_o = samlTypeDao.findByNameAndProject(s, pro).get(0);
        boolean flag = false;
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.FuncStruct, s_o.getId(), f_o.getId(), Data.field2Type);
        flag = !relation.isEmpty();

        String exp = f_o.getName() + "--≪FuncStruct≫-->" + s_o.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.FuncStructOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.FuncStructError, r, Data.RelaTitle, rep_id);
        }
        return flag;
    }

    /**
     * 判断成员函数关系。FuncStruct 【f--struct，不是fList--struct】
     *
     * 公式TR12-2：
     *      (Γ⊢T_1,   Γ⊢T_2,   Γ⊢T_3,   T^struct={T_vars=[l:T_3 ]^*,T_funcs={[l:T_1→T_2 ]^* }})
     *      ——————————————————————————————————————————————————————————————————————————————————————
     *      (□(T_funcs →┴(≪func≫) )T^struct  或  T_funcs= T^struct.funcs 或 TR24-2(T_funcs,T^struct))
     *
     *
     * 问题：到底是list与struct的关系还是type与struct的关系？list-struct vs type-struct？
     *      list-struct:【先搁置，看最终验证是否需要再说。】
     *          传参要改成(SamlList fs, SamlType struct)
     *
     *      type-struct:【暂用这个】
     *          方法1：f.id在t.list2的member_type中。————generateFunctionStruct
     *          方法2：看relation表中是否有f.id-struct.id的关系。【其实方法2是方法1的结论，即方法1生成关系】
     *
     */
    @Override
    public boolean TR12_2(SamlType f, SamlType struct, String rep_id) {
        boolean flag = false;

        ////方法1：生成关系再判断，用于局部。
        //String[] arr = samlListDao.findSamlListById(struct.getList2()).getMemberType().split(",");
        //String fid = f.getId() + "";
        //for (String s : arr) {
        //    if (s.equals(fid)) {
        //        flag = true;
        //        break;
        //    }
        //}

        //方法2：如果提前已经生成过了relation表，那么就用方法2，否则用方法1。方法2为全局，方法1为局部。
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.FuncStruct, struct.getId(), f.getId(), Data.field2Type);
        flag = !relation.isEmpty();

        String exp = f.getName() + "--≪FuncStruct≫-->" + struct.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.FuncStructOK, r, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.FuncStructError, r, rep_id);
        }

        return flag;
    }

    @Override
    public boolean TR13_1(int f, int file, String rep_id) {
        SamlType f_o = samlTypeDao.findSamlTypeById(f);
        SamlType file_o = samlTypeDao.findSamlTypeById(file);

        List<SamlRelation> relation = samlRelationDao.findRelation(Data.FuncFile, file, f, Data.field2Type);
        boolean flag = !relation.isEmpty();

        String exp = f_o.getName() + "--≪FuncFile≫-->" + file_o.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.FuncFileOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.FuncFileError, r, Data.RelaTitle, rep_id);
        }
        return flag;
    }

    @Override
    public boolean TR13_1(String f, String file, String rep_id, int pro) {
        SamlType f_o = samlTypeDao.findByNameAndProject(f, pro).get(0);
        SamlType file_o = samlTypeDao.findByNameAndProject(file, pro).get(0);

        List<SamlRelation> relation = samlRelationDao.findRelation(Data.FuncFile, file_o.getId(), f_o.getId(), Data.field2Type);
        boolean flag = !relation.isEmpty();

        String exp = f_o.getName() + "--≪FuncFile≫-->" + file_o.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.FuncFileOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.FuncFileError, r, Data.RelaTitle, rep_id);
        }
        return flag;
    }

    /**
     * 判断全局函数关系。FuncFile【f--file，不是fList--file】
     *
     * 公式TR13：
     *      (Γ⊢T_1,   Γ⊢T_2,   Γ⊢T^struct,   T^file={funcs:{[l:T_1→T_2 ]^* },   structs:[t:T^struct ]^* })
     *      ——————————————————————————————————————————————————————————————————————————————————————————————
     *      ( f:(T_1 □(→)T_2)□(→┴(≪FuncFile≫) )T^file 或 f:(T_1 □(→)T_2)∈ T^file.funcs )
     *
     */
    @Override
    public boolean TR13_1(SamlType f, SamlType file, String rep_id) {
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.FuncFile, file.getId(), f.getId(), Data.field2Type);
        boolean flag = !relation.isEmpty();

        String exp = f.getName() + "--≪FuncFile≫-->" + file.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.FuncFileOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.FuncFileError, r, Data.RelaTitle, rep_id);
        }
        return flag;
    }

    @Override
    public boolean TR13_2(SamlType s, SamlType file, String rep_id) {
        List<SamlRelation> relation = samlRelationDao.findRelation(Data.StructFile, file.getId(), s.getId(), Data.field2Type);
        boolean flag = !relation.isEmpty();

        String exp = s.getName() + "--≪StructFile≫-->" + file.getName();
        if (flag) {
            SamlRelation r = relation.get(0);
            r.setExp(exp);
            reportService.saveError(Data.StructFileOK, r, Data.RelaTitle, rep_id);
        } else {
            SamlRelation r = new SamlRelation(exp);
            reportService.saveError(Data.StructFileError, r, Data.RelaTitle, rep_id);
        }
        return flag;
    }

}
