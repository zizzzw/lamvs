package com.example.dynamic_validate.service.imp;

import com.example.dynamic_validate.dao.DemandInvokeDao;
import com.example.dynamic_validate.dao.SamlTypeDao;
import com.example.dynamic_validate.dao.SamlTypeDependDao;
import com.example.dynamic_validate.data.Data;
import com.example.dynamic_validate.entity.SamlType;
import com.example.dynamic_validate.entity.SamlTypeDepend;
import com.example.dynamic_validate.service.GenerateListService;
import com.example.dynamic_validate.service.GenerateTypeService;
import com.example.dynamic_validate.service.SamlListService;
import com.example.dynamic_validate.service.StatisticService;
import com.example.dynamic_validate.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticServiceImp implements StatisticService {
    @Value("${lamvs.project}")
    private int pro;
    @Autowired
    private DemandInvokeDao demandInvokeDao;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private GenerateTypeService generateTypeService;
    @Autowired
    private SamlTypeDependDao samlTypeDependDao;
    @Autowired
    private GenerateListService generateListService;
    @Autowired
    private SamlListService samlListService;

    /**
     * 函数提取
     * 表达式OK
     *
     */
    @Override
    public StringBuilder getAllFunc() {
        List<SamlType> list = new ArrayList<>();
        String fName = demandInvokeDao.findAllFunc1Name() + "," + demandInvokeDao.findAllFunc2Name();
        fName = StrUtil.distinct(fName);
        System.out.println("依赖函数names：" + fName);

        String fstr = demandInvokeDao.findAllFunc1() + "," + demandInvokeDao.findAllFunc2();
        fstr = StrUtil.distinct(fstr);
        String[] fids = fstr.split(",");
        for (String s : fids) {
            if (s.equals("")) continue;
            int id = Integer.parseInt(s);
            SamlType t = samlTypeDao.findSamlTypeById(id);
            System.out.println(t);
            list.add(t);
            generateListService.generFuncListByExp(t);
            SamlTypeDepend exist = samlTypeDependDao.findSamlTypeDependById(id);
            if (exist == null) {
                samlTypeDependDao.insertFromType(id);
            } else {
                samlTypeDependDao.deleteById(id);
                samlTypeDependDao.insertFromType(id);
            }
        }
        StringBuilder idstr = new StringBuilder(fstr);
        System.out.println("依赖函数个数：" + list.size());
        return idstr;
    }

    /**
     * 把表达式更新为alias的原型。
     */
    public void updateExp12Alias(SamlType t) {
        int id = t.getId();
        String exp1 = generateListService.alias2OriginalType(t.getList1Exp());
        String exp2 = generateListService.alias2OriginalType(t.getList2Exp());
        samlTypeDao.updateList1exp(id, exp1);
        samlTypeDao.updateList2exp(id, exp2);
    }

    @Override
    public void copyAllDps(String ids) {
        String[] idarr = ids.split(",");
        for (String s : idarr) {
            if (s.equals("")) continue;
            int id = Integer.parseInt(s);
            SamlTypeDepend exist = samlTypeDependDao.findSamlTypeDependById(id);
            if (exist == null) {
                samlTypeDependDao.insertFromType(id);
            } else {
                samlTypeDependDao.deleteById(id);
                samlTypeDependDao.insertFromType(id);
            }
        }
    }

    /**
     * 获取全部依赖类型
     */
    @Override
    public List<SamlType> getAllDepends() {
        StringBuilder idList = new StringBuilder();
        List<SamlType> list = new ArrayList<>();
        List<String> depList = new ArrayList<>();
        List<String> noList = new ArrayList<>();
        String depstr = samlTypeDependDao.findAllList1Exp() + "," + samlTypeDependDao.findAllList2Exp();
        System.out.println("去重前的依赖类型str：" + depstr);
        depstr = StrUtil.distinct(depstr);
        System.out.println("去重后的依赖类型str：" + depstr);
        String[] tnames = depstr.split(",");
        for (String s : tnames) {
            if (s.equals("")) continue;
            depList.add(s);
            List<SamlType> ts = samlTypeDao.findByName(s);
            if (ts == null || ts.isEmpty()) {
                System.out.println("依赖类型不存在：" + s);
                noList.add(s);
            } else if (ts.size() == 1) {
                SamlType t = ts.get(0);
                System.out.println(t.getId() + ":" + t.getExp());
                list.add(t);
                idList.append(t.getId());
                if (t.getLevel() == Data.BASE) {
                    System.out.println("Base类型，不用解析：" + t.getExp());
                } else if (t.getLevel() == Data.FUNC) {
                    generateListService.generFuncListByExp(t);
                } else if (t.getLevel() == Data.STRUCT) {
                    generateListService.generStructListByExp(t);
                } else {
                    System.out.println(t.getName() + "不知道是什么鬼类型，没法解析：" + t.getLevel());
                }
                SamlTypeDepend exist = samlTypeDependDao.findSamlTypeDependById(t.getId());
                if (exist == null) {
                    samlTypeDependDao.insertFromType(t.getId());
                } else {
                    samlTypeDependDao.deleteById(t.getId());
                    samlTypeDependDao.insertFromType(t.getId());
                }
            } else { // 重名的类型【有一个办法，全部存上再说】
                System.out.println("以下类型重名了，无法判断哪个，先都存上再说：");
                for (SamlType t : ts) {
                    list.add(t);
                    System.out.println(t.getName());
                    if (t.getLevel() == Data.FUNC) {
                        generateListService.generFuncListByExp(t);
                    } else if (t.getLevel() == Data.STRUCT) {
                        generateListService.generStructListByExp(t);
                    } else {
                        System.out.println(t.getName() + "不知道是什么鬼类型，没法解析：" + t.getLevel());
                    }
                    SamlTypeDepend exist = samlTypeDependDao.findSamlTypeDependById(t.getId());
                    if (exist == null) {
                        samlTypeDependDao.insertFromType(t.getId());
                    } else {
                        samlTypeDependDao.deleteById(t.getId());
                        samlTypeDependDao.insertFromType(t.getId());
                    }
                }
            }
        }
        System.out.println("依赖类型数" + tnames.length + "," + depList);
        System.out.println("其中不存在的类型数：" + noList.size() + "，" + noList);
        System.out.println("存在依赖类型数：" + list.size() + "，" + list);

        return list;
        //return idList;
    }

    @Override
    public List<List<SamlType>> splitByLevel(List<SamlType> dpList) {
        //int[] num_t = {0, 0, 0, 0};// base,func,struct,other
        List<SamlType> baseList = new ArrayList<>();
        List<SamlType> funcList = new ArrayList<>();
        List<SamlType> structList = new ArrayList<>();
        List<SamlType> otherList = new ArrayList<>();
        for (SamlType t : dpList) {
            if (t.getLevel() == Data.BASE) {
                //num_t[0] += 1;
                baseList.add(t);
                //System.out.println(t.getExp() + "正确");
            } else if (t.getLevel() == Data.FUNC) {
                //num_t[1] += 1;
                funcList.add(t);
            } else if (t.getLevel() == Data.STRUCT) {
                //num_t[2] += 1;
                structList.add(t);
            } else {
                //num_t[3] += 1;
                otherList.add(t);
            }
        }
        List<List<SamlType>> list = new ArrayList<>();
        list.add(baseList);
        list.add(funcList);
        list.add(structList);
        list.add(otherList);
        return list;
    }

    /**
     * 获取类型依赖列表
     */
    @Override
    public List<String> getTypeDeps(List<SamlType> dpList) {
        StringBuilder tdpList = new StringBuilder(); // 有依赖类型的依赖类型
        StringBuilder typesdpList = new StringBuilder(); //有依赖的类型
        StringBuilder tnodpList = new StringBuilder(); // 存在即正确的类型，没有依赖
        for (SamlType t : dpList) {
            int level = t.getLevel();
            if (level == Data.BASE) {
                System.out.println(t.getExp() + "正确");
                System.out.println("基本类型没有依赖");
                tnodpList.append(t.getName() + ",");
            } else if (level == Data.FUNC || level == Data.STRUCT) {
                String str = t.getList1Exp() + "," + t.getList2Exp() + ",";
                tdpList.append(str);
                typesdpList.append(t.getName() + ",");
            } else {
                if (t.getList1Exp() != null && !t.getList1Exp().equals("")) {
                    String str = t.getList1Exp() + "," + t.getList2Exp() + ",";
                    tdpList.append(str);
                    typesdpList.append(t.getName() + ",");
                } else {
                    tnodpList.append(t.getName() + ",");
                    System.out.println(t.getExp() + "正确");
                    System.out.println("层次为" + level + "的类型没有依赖");
                }
            }
        }
        List<String> li = new ArrayList<>();
        li.add(StrUtil.distinct(String.valueOf(tnodpList)));
        li.add(StrUtil.distinct(String.valueOf(typesdpList)));
        String str = StrUtil.distinct(String.valueOf(tdpList));
        str = generateListService.alias2OriginalType(str); // 替换成原类型
        li.add(str);
        return li;
    }

    /**
     * 获取依赖结果。
     */
    @Override
    public List<String> getReList(String tdpList) {
        StringBuilder ids = new StringBuilder();

        List<String> reList = new ArrayList<>(); // 存在的，重复的，缺的。
        StringBuilder existList = new StringBuilder();
        StringBuilder mulitList = new StringBuilder();
        StringBuilder lackList = new StringBuilder();
        String[] sl = tdpList.split(",");
        for (String s : sl) {
            if (s.equals("")) continue;
            List<SamlType> list = samlTypeDao.findByName(s);
            if (list == null || list.isEmpty()) {
                lackList.append(s + ",");
            } else if (list.size() == 1) {
                existList.append(s + ",");
                ids.append(list.get(0).getId() + ",");
            } else {
                mulitList.append(s + ",");
                for (SamlType t : list) {
                    ids.append(t.getId() + ",");
                }
            }
        }
        reList.add(StrUtil.distinct(String.valueOf(existList)));
        reList.add(StrUtil.distinct(String.valueOf(mulitList)));
        reList.add(StrUtil.distinct(String.valueOf(lackList)));
        reList.add(StrUtil.distinct(String.valueOf(ids)));
        return reList;
    }

    @Override
    public void generFuncExp(List<SamlType> funcs) {
        //把函数表达式解析出来
        if (funcs == null || funcs.size() == 0) {
            return;
        }
        for (int i = 0; i < funcs.size(); i++) {
            SamlType curFunc = funcs.get(i);
            generateListService.generFuncListByExp(curFunc);
        }
    }

    @Override
    public void generFiles(List<SamlType> flist) {
        List<String> allPath = new ArrayList<>();
        for (int i = 0; i < flist.size(); i++) {
            String path = flist.get(i).getPath();
            if (allPath.contains(path)) continue;
            allPath.add(path);
            System.out.println(path);
            generateTypeService.generOneFileByPath(path, pro);
        }
        System.out.println("所有path的个数：" + allPath.size());
    }

    @Override
    public void generSubsysDentry() {

    }
}
