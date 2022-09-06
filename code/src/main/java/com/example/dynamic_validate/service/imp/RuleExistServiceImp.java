package com.example.dynamic_validate.service.imp;

import com.example.dynamic_validate.dao.BaseTypeDao;
import com.example.dynamic_validate.dao.SamlListDao;
import com.example.dynamic_validate.dao.SamlTypeDao;
import com.example.dynamic_validate.data.Data;
import com.example.dynamic_validate.entity.SamlList;
import com.example.dynamic_validate.entity.SamlType;
import com.example.dynamic_validate.service.ReportService;
import com.example.dynamic_validate.service.RuleExistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleExistServiceImp implements RuleExistService {

    @Autowired
    private BaseTypeDao baseTypeDao;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private ReportService reportService;
    @Autowired
    private SamlListDao samlListDao;


    /**
     * 判断是不是base类型，且存在。
     *
     * 这个地方，我觉得传一个id或name更好，
     * 要不你传个对象，那本来就查出来了才是对象啊，就没得玩了嘛。
     *
     * 2022年4月13日19:26:39  增加两个同名方法：传参int id 或String name
     */
    @Override
    public boolean TR01(int id, String rep_id) {
        boolean flag = false;
        SamlType t = samlTypeDao.findSamlTypeById(id);
        if (t == null) {
            //reportService.saveError(Data.BaseLackError, null, rep_id); // id是不显示的？
            return flag;
        } else {
            flag = t.getLevel() == Data.BASE;
            //if (flag) {
            //    reportService.saveError(Data.BaseOK, t, rep_id);
            //} else {
            //    reportService.saveError(Data.BaseLevelError, t, rep_id); // 存在，但是层次错误，不是Base类型
            //}
            return flag;
        }
    }

    @Override
    public boolean TR01(String name, String rep_id) {
        boolean flag = false;
        List<SamlType> ts = samlTypeDao.findByName(name);
        SamlType o = new SamlType(name);

        if (ts == null || ts.isEmpty()) {
            //reportService.saveError(Data.BaseLackError, o, rep_id); // name显示
            return flag;
        } else {
            for (SamlType t : ts) {
                flag = t.getLevel() == Data.BASE;
                if (flag) break;
            }
            //if (flag) {
            //    reportService.saveError(Data.BaseOK, o, rep_id);
            //} else {
            //    reportService.saveError(Data.BaseLevelError, o, rep_id); // 存在，但是层次错误，不是Base类型
            //}
            return flag;
        }
    }

    @Override
    public boolean TR01(SamlType samlType, String rep_id) {
        if (samlType == null) {
            //errList.add(new ErrorClassify(Data.NullError, err));
            //System.out.println(err);
            //reportService.saveError(Data.NullError, samlType, rep_id);
            return false;
        } else if (samlType.getLevel() != Data.BASE) {
            //reportService.saveError(Data.BaseLevelError, samlType, rep_id);
            return false;
        }

        //BaseType cur = baseTypeDao.findBaseTypeByName(name);
        boolean exist = samlTypeDao.existsById(samlType.getId());
        if (exist) {
            //String err = Data.OK + ":当前类型" + name + "是base类型且存在";
            //reportService.saveError(Data.OK, samlType, rep_id);
            return true;
        } else {
            //reportService.saveError(Data.BaseLackError, samlType, rep_id);
            return false;
        }
    }

    /**
     * 判断是不是func类型且存在。
     */
    @Override
    public boolean TR02(int id, String rep_id) {
        SamlType t = samlTypeDao.findSamlTypeById(id);
        return t != null && t.getLevel() == Data.FUNC;
    }

    @Override
    public boolean TR02(SamlType samlType, String rep_id) {
        if (samlType == null) {
            //reportService.saveError(Data.NullError, samlType, rep_id);
            return false;
        } else if (samlType.getLevel() != Data.FUNC) {
            //reportService.saveError(Data.FuncLevelError, samlType, rep_id);
            return false;
        }

        boolean exist = samlTypeDao.existsById(samlType.getId());
        if (exist) {
            //reportService.saveError(Data.OK, samlType, rep_id);
            return true;
        } else {
            //reportService.saveError(Data.FuncLackError, samlType, rep_id);
            return false;
        }
    }

    @Override
    public boolean TR03(int id, String rep_id) {
        SamlType t = samlTypeDao.findSamlTypeById(id);

        boolean flag = t == null;
        if (flag) {
            //reportService.saveError(Data.StructLackError, t, rep_id);
            return false;
        } else {
            flag = (t.getLevel() == Data.STRUCT || t.getLevel() == Data.ENUM || t.getLevel() == Data.UNION); // 【这里错了吧！】
            //if (flag) {
            //    //reportService.saveError(Data.OK, t, rep_id);
            //} else {
            //    //reportService.saveError(Data.StructLevelError, t, rep_id);
            //}
        }
        return flag;
    }

    @Override
    public boolean TR03(String name, String rep_id) {
        boolean flag = false;
        List<SamlType> ts = samlTypeDao.findByName(name);
        SamlType o = new SamlType(name);

        if (ts == null || ts.isEmpty()) {
            //reportService.saveError(Data.StructLackError, o, rep_id); // name显示
            return flag;
        } else {
            for (SamlType t : ts) {
                flag = t.getLevel() == Data.STRUCT;
                if (flag) break;
            }
            //if (flag) {
            //    reportService.saveError(Data.StructOK, o, rep_id);
            //} else {
            //    reportService.saveError(Data.StructLevelError, o, rep_id); // 存在，但是层次错误，不是Base类型
            //}
            return flag;
        }
    }

    /**
     * 判断是不是struct类型且存在。
     */
    @Override
    public boolean TR03(SamlType t, String rep_id) {
        if (t == null) {
            //reportService.saveError(Data.NullError, t, rep_id);
            return false;
        } else if (!(t.getLevel() == Data.STRUCT || t.getLevel() == Data.ENUM || t.getLevel() == Data.UNION)) {
            //reportService.saveError(Data.StructLevelError, t, rep_id);
            return false;
        }

        boolean exist = samlTypeDao.existsById(t.getId());
        if (exist) {
            //reportService.saveError(Data.OK, t, rep_id);
            return true;
        } else {
            //reportService.saveError(Data.StructLackError, t, rep_id);
            return false;
        }
    }

    @Override
    public boolean TR04(int id, String rep_id) {
        SamlType t = samlTypeDao.findSamlTypeById(id);

        boolean flag = t == null;
        if (flag) {
            //reportService.saveError(Data.FileLackError, t, rep_id);
            return false;
        } else {
            flag = t.getLevel() == Data.FILE;
            //if (flag) {
            //    reportService.saveError(Data.FileOK, t, rep_id);
            //} else {
            //    reportService.saveError(Data.FileLevelError, t, rep_id);
            //}
        }
        return flag;
    }

    @Override
    public boolean TR04(String name, String rep_id) {
        boolean flag = false;
        List<SamlType> ts = samlTypeDao.findByName(name);
        SamlType o = new SamlType(name);

        if (ts == null || ts.isEmpty()) {
            //reportService.saveError(Data.FileLackError, o, rep_id); // name显示
            return flag;
        } else {
            for (SamlType t : ts) {
                flag = t.getLevel() == Data.FILE;
                if (flag) break;
            }
            //if (flag) {
            //    reportService.saveError(Data.FileOK, o, rep_id);
            //} else {
            //    reportService.saveError(Data.FileLevelError, o, rep_id); // 存在，但是层次错误，不是Base类型
            //}
            return flag;
        }
    }

    /**
     * 判断是不是file类型且存在。
     */
    @Override
    public boolean TR04(SamlType samlType, String rep_id) {
        if (samlType == null) {
            //reportService.saveError(Data.NullError, null, rep_id);
            return false;
        } else if (samlType.getLevel() != Data.FILE) {
            //reportService.saveError(Data.FileLevelError, samlType, rep_id);
            return false;
        }

        boolean exist = samlTypeDao.existsById(samlType.getId());
        if (exist) {
            //reportService.saveError(Data.OK, samlType, rep_id);
            return true;
        } else {
            //reportService.saveError(Data.FileLackError, samlType, rep_id);
            return false;
        }
    }

    /**
     * 判断是不是dentry类型且存在。
     */
    @Override
    public boolean TR05(SamlType samlType, String rep_id) {
        if (samlType == null) {
            //reportService.saveError(Data.NullError, samlType, rep_id);
            return false;
        } else if (samlType.getLevel() != Data.DENTRY) {
            //reportService.saveError(Data.DentryLevelError, samlType, rep_id);
            return false;
        }

        boolean exist = samlTypeDao.existsById(samlType.getId());
        if (exist) {
            //reportService.saveError(Data.OK, samlType, rep_id);
            return true;
        } else {
            //reportService.saveError(Data.DentryLackError, samlType, rep_id);
            return false;
        }
    }

    /**
     * 判断是不是subsys类型且存在。
     */
    @Override
    public boolean TR06(SamlType samlType, String rep_id) {
        if (samlType == null) {
            //reportService.saveError(Data.NullError, samlType, rep_id);
            return false;
        } else if (samlType.getLevel() != Data.SUBSYS) {
            //reportService.saveError(Data.SubsysLevelError, samlType, rep_id);
            return false;
        }

        boolean exist = samlTypeDao.existsById(samlType.getId());
        if (exist) {
            //reportService.saveError(Data.OK, samlType, rep_id);
            return true;
        } else {
            //reportService.saveError(Data.SubsysLackError, samlType, rep_id);
            return false;
        }
    }

    /**
     * 判断是不是list复合类型且存在。
     *
     * 复合方式有很多，这里到底指的是哪种，要不要加个参数？X
     * 或者，定个范围[1,9] √
     *
     */
    @Override
    public boolean TR07(SamlList samlList, String rep_id) {
        if (samlList == null) {
            //reportService.saveError(Data.NullError, null, rep_id);
            return false;
        } else if (samlList.getListCompound() < 1 || samlList.getListCompound() > 9) {
            //reportService.saveError(Data.ListCompoundError, samlList, rep_id);
            return false;
        }

        boolean exist = samlListDao.existsById(samlList.getId());
        if (exist) {
            //reportService.saveError(Data.OK, samlList, rep_id);
            return true;
        } else {
            //reportService.saveError(Data.ListLackError, samlList, rep_id);
            return false;
        }
    }

    /**
     * 判断是不是level层类型且存在。
     *
     * TR01~TR06的缩写。
     */
    @Override
    public boolean TR0X(SamlType samlType, int level) {
        return false;
    }
}
