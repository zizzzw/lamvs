package com.example.dynamic_validate.service.imp;

import com.example.dynamic_validate.dao.*;
import com.example.dynamic_validate.data.Data;
import com.example.dynamic_validate.entity.*;
import com.example.dynamic_validate.service.*;
import com.example.dynamic_validate.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SamlTypeServiceImp implements SamlTypeService {
    @Value("${veri.selfCheck.type.fromFirstNotdeal}")
    private boolean fromFirstNotdeal;

    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private SamlTypeLevelDao samlTypeLevelDao;
    @Autowired
    private SamlListDao samlListDao;
    @Autowired
    private RuleExistService ruleExistService;
    @Autowired
    private IncludeImportDao includeImportDao;
    @Autowired
    private SamlTypeMacroDao samlTypeMacroDao;
    @Autowired
    private ReportService reportService;
    @Autowired
    private TypeLackDao typeLackDao;
    @Autowired
    private SamlListService samlListService;
    @Autowired
    private RuleService ruleService;

    /**
     * 检测saml_type表中，某个level的所有Type，其包含元素是否存在。
     *
     * 判断标准是涉及的list是否存在，涉及的list类型是否和level对应正确。
     * 1、涉及的father存在
     *      father字段在type的id中可查到
     * 2、涉及的list存在
     *      list1和list2字段在list的id中可查到
     * 2、list的level正确
     *      //level=4-7，都是只有一个list1,c=1，第二个list2=0
     *      //level=3，接口，2个list，list1=var_list，c=6；list2=function_list,c=7
     *          struct中的function是需要集合为一个list的，而全局function是不需要集合为一个list的。
     *      //level=2，只考虑了函数，2个list，list1=in_list，list2=out_list,都是c=8
     *      //level=1，father == 0 && list1_id == 0 && list2_id == 0 。
     *
     * @param level
     * @param rep_id
     * @return 哪个Type出错了，String打印出来，没出错就打印出type_name存在。如果检测完成，没报错，就返回true，报错肯定抛异常了。
     */
    public void typeCheckByLevel(int level, int pro, String rep_id) {
        //ArrayList<SamlType> rightTypeList = new ArrayList<SamlType>();//待返回的正确类型列表，暂时不用
        System.out.println();
        List<SamlType> typeByLevel;
        if (fromFirstNotdeal) {
            typeByLevel = samlTypeDao.findByLevelAndProjectValid2(level, pro);
        } else {
            typeByLevel = samlTypeDao.findByLevelAndProject(level, pro);
        }

        System.out.println("\nlevel " + level + " " + samlTypeLevelDao.findById(level).get().getName() + " 开始检测......");

        for (int i = 0; i < typeByLevel.size(); i++) {
            SamlType samlType_i = typeByLevel.get(i);
            int id = samlType_i.getId();
            //boolean flag = typeCheckById(id);
            //boolean flag = typeCheckByListValid(samlType_i);
            ruleService.DEV_SR(samlType_i, rep_id); // 这里其实已经更新了type的Vali了，下面没必要了。
        }

        System.out.println("level " + level + " " + samlTypeLevelDao.findById(level).get().getName() + " 结束检测\n");

    }

    /**
     * 根据当前type的list1和list2情况，判断当前type是否正确
     *
     * 实际上，按理说应该直接用DEV_SR验证的！当前这个有点水。
     * 好吧，不用这个了，直接上DEV_SR好了。
     */
    @Deprecated
    private boolean typeCheckByListValid(SamlType type) {
        boolean flag;
        int id = type.getId();
        int father = type.getFather();
        int list1_id = type.getList1();
        int list2_id = type.getList2();
        int level = type.getLevel();
        int list1_vali, list2_vali;
        if (list1_id == 0 && list2_id == 0) {
            System.out.println("Id:" + id + ",类型\"" + type.getExp() + "\"表达式list1和list2同时为空，不正确！");
            flag = false;
        } else if (list1_id != 0 && list2_id == 0) {
            list1_vali = samlListDao.findSamlListById(list1_id).getValid();
            flag = getListVali(list1_id, list1_vali);
        } else if (list1_id == 0 && list2_id != 0) {
            if (level == 2) { //函数的输出是typeId而非listId
                list2_vali = samlTypeDao.findSamlTypeById(list2_id).getValid();
            } else {
                list2_vali = samlListDao.findSamlListById(list2_id).getValid();
            }
            flag = getListVali(list2_id, list2_vali);
        } else { // 两个都存在
            list1_vali = samlListDao.findSamlListById(list1_id).getValid();

            if (level == 2) { //函数的输出是typeId而非listId
                list2_vali = samlTypeDao.findSamlTypeById(list2_id).getValid();
            } else {
                list2_vali = samlListDao.findSamlListById(list2_id).getValid();
            }
            flag = getListVali(list1_id, list1_vali) && getListVali(list2_id, list2_vali);
        }
        return flag;
    }

    /**
     * 根据list_valid检查当前list的真假，如果未验证，就需要先用checkOneList验出结果
     */
    private boolean getListVali(int list_id, int list_vali) {
        boolean flag;
        if (list_vali == Data.Valid1) {
            flag = true;
        } else if (list_vali == Data.Valid0) {
            flag = false;
        } else {
            SamlList list2 = samlListDao.findSamlListById(list_id);
            flag = samlListService.checkOneList(list2);
        }
        return flag;
    }

    @Override
    @Deprecated
    public boolean typeCheckById(int id) {
        SamlType type = samlTypeDao.getOne(id);
        int father = type.getFather();
        int list1_id = type.getList1();
        int list2_id = type.getList2();
        int level = type.getLevel();

        //level=1，根本不用检测，目前单独在base_type表中。
        //如果想检查系统中是否存在，可以弄一个List存储所有系统中的类名，然后，在里面就行。
        if (level == 1) {
            if (father == 0 && list1_id == 0 && list2_id == 0) {
                System.out.println("Id:" + id + ",java语言基本类型\"" + type.getName() + "\"存在 | 条件：father == 0 && list1_id == 0 && list2_id == 0");
                //更新state字段为有效
                //samlTypeDao.updateValid(id, 1);
                return true;
            } else {
                //samlTypeDao.updateValid(id, 0);
                return false;
            }
            //return ruleExistService.TR01(id);
        } else {
            boolean flag_father = false;
            boolean flag_list = false;

            //判断father：
            //level=6的father=0就是正常的！
            //其他level，father在type表中存在，可以结束了；father是正确类型，需要递归，或者再在father那个Type判断！【感觉递归模型太大不允许，可能内存溢出】

            if (level == 6) {
                if (father == 0) {
                    flag_father = true;
                } else {
                    System.out.println("Id:" + id + ",类型\"" + type.getName() + "\"是顶层，表达式father字段应该为空");
                }
            } else {
                if (samlTypeDao.existsById(father)) {
                    flag_father = true;
                } else {
                    System.out.println("Id:" + id + ",类型\"" + type.getName() + "\"表达式缺father字段");
                }
            }

            //判断saml_list1：
            //level=4-6，都是只有一个list1,c=1,list2=0
            //level=3，struct接口,2个list,list1=element_list,c=6;list2=function_list,c=7
            //level=2，只考虑了方法,2个list,list1=in_list,list2=out_list,都是c=8
            //level=1，expression==0就对了。
            //saml_type中没有存储成员属性，但是还是需要考虑成员属性的？成员属性就存到了saml_list中作为复合类型考虑？

            if (level <= 6 && level >= 4) {
                if (list2_id == 0
                        && samlListDao.existsById(list1_id)
                        && samlListDao.findById(list1_id).get().getListCompound() == 1) {
                    flag_list = true;
                } else {
                    System.out.println("Id:" + id + ",类型\"" + type.getExp() + "\"表达式缺list1字段，或者list1不正确，或者list2字段应该为空");
                }
            } else if (level == 3) {

                if ((samlListDao.existsById(list1_id) && samlListDao.findById(list1_id).get().getListCompound() == 6)
                        || (samlListDao.existsById(list2_id) && samlListDao.findById(list2_id).get().getListCompound() == 7)) {
                    flag_list = true;
                } else {
                    System.out.println("Id:" + id + ",结构体\"" + type.getExp() + "\"表达式缺list1或list2字段，或者list1不是属性，或者list2不是方法");
                }
            } else if (level == 2) {
                if (samlListDao.existsById(list1_id) && samlListDao.existsById(list2_id)
                        && samlListDao.findById(list1_id).get().getListCompound() == 8
                        && samlListDao.findById(list2_id).get().getListCompound() == 8) {
                    flag_list = true;
                } else {
                    System.out.println("Id:" + id + ",函数\"" + type.getExp() + "\"表达式缺list1或list2字段，或者list1不正确，或者list2不正确");
                }
            }

            if (flag_father && flag_list) {
                System.out.println("Id:" + id + ",类型Name:" + type.getExp() + "，Scope:" + type.getScope() + "，存在");
                samlTypeDao.updateValid(id, 1);
                return true;
            }
        }
        samlTypeDao.updateValid(id, 0);
        return false;
    }

    /**
     *获取函数依赖的参数和返回值类型。
     *由于先验证的函数，通过后，才来获取依赖类型。所以，只要能到这一步，不会缺。缺的话根本到不了这一步！
     */
    @Override
    public List<Integer> getFuncDependParaAndReturn(SamlType func) {
        List<Integer> list = new ArrayList<>();
        int list1Id = func.getList1();
        int returnId = func.getList2(); // 直接就是returnId
        if (func.getList2Exp().equals("")) {
            System.out.println("函数的返回值不存在，不应该的！函数必须有返回值！");
            return null;
        }
        if (list1Id == 0) {
            //SELECT * FROM saml_type WHERE `level`=2 AND list1 = 0
            System.out.println("大概率不会出现，目前为止还没找到没参数的函数！");
        }

        String[] list1exp = func.getList1Exp().split(",");
        String[] paramsId = samlListDao.findSamlListById(list1Id).getMemberType().split(",");
        for (int i = 0; i < paramsId.length; i++) {
            if (paramsId[i].equals("") || paramsId[i].equals("0")) {
                System.out.println("这种情况就不会出现：参数依赖类型" + list1exp[i] + "不存在");
            }
            int id = Integer.parseInt(paramsId[i]);
            list.add(id);
        }
        list.add(returnId);
        return list;
    }

    public List<SamlType> getFuncDependParaAndReturn1(SamlType func) {
        List<SamlType> list = new ArrayList<>();
        int list1Id = func.getList1();
        int returnId = func.getList2(); // 直接就是returnId
        if (func.getList2Exp().equals("")) {
            System.out.println("函数的返回值不存在，不应该的！函数必须有返回值！");
            return null;
        }
        if (list1Id == 0) {
            //SELECT * FROM saml_type WHERE `level`=2 AND list1 = 0
            System.out.println("大概率不会出现，目前为止还没找到没参数的函数！");
        }

        String[] list1exp = func.getList1Exp().split(",");
        String[] paramsId = samlListDao.findSamlListById(list1Id).getMemberType().split(",");
        for (int i = 0; i < paramsId.length; i++) {
            if (paramsId[i].equals("") || paramsId[i].equals("0")) {
                System.out.println("这种情况就不会出现：参数依赖类型" + list1exp[i] + "不存在");
            }
            int id = Integer.parseInt(paramsId[i]);
            list.add(samlTypeDao.findById(id).get());
        }
        list.add(samlTypeDao.findById(returnId).get());
        return list;
    }

    public List<SamlType> getFuncDependParaAndReturn1(SamlType func, List<ErrorClassify> errList) {
        List<SamlType> list = new ArrayList<>();
        int list1Id = func.getList1();
        int returnId = func.getList2(); // 直接就是returnId
        if (func.getList2Exp().equals("")) {
            System.out.println("函数的返回值不存在，不应该的！函数必须有返回值！");
            return null;
        }
        if (list1Id == 0) {
            //SELECT * FROM saml_type WHERE `level`=2 AND list1 = 0
            System.out.println("大概率不会出现，目前为止还没找到没参数的函数！");
        }

        String[] list1exp = func.getList1Exp().split(",");
        String[] paramsId = samlListDao.findSamlListById(list1Id).getMemberType().split(",");
        for (int i = 0; i < paramsId.length; i++) {
            if (paramsId[i].equals("") || paramsId[i].equals("0")) {
                System.out.println("参数依赖类型" + list1exp[i] + "不存在");
                SamlType t = new SamlType(list1exp[i]);
                //reportService.saveError(Data.FuncParamLackError, t, report_id);
            }
            int id = Integer.parseInt(paramsId[i]);

            list = findTypeById(id, Data.FuncParamLackError, list, errList);
        }
        list = findTypeById(returnId, Data.FuncReturnLackError, list, errList);

        return list;
    }

    /**
     * 如果存在，把对应类型加到list列表中，返回列表。
     * 如果不存在，打印错误报告。
     */
    private List<SamlType> findTypeById(int id, String
            errCode, List<SamlType> list, List<ErrorClassify> errList) {

        SamlType samlType = samlTypeDao.findSamlTypeById(id);
        if (samlType == null) {
            String err = errCode + ":函数依赖类型Id" + id + "不存在";
            System.out.println(err);
            errList.add(new ErrorClassify(errCode, err));
        } else {
            String err = Data.OK + ":函数依赖类型" + samlType.getName() + "存在";
            System.out.println(err);
            errList.add(new ErrorClassify(Data.OK, err));

            list.add(samlType);
        }
        return list;
    }

    @Override
    public List<Integer> getFuncRecurFathers(SamlType func) {
        if (func.getLevel() != 2) {
            System.out.println(func.getName() + "不是函数！");
            return null;
        }
        List<Integer> list = new ArrayList<>();
        //方法1：
        list = getRecurFather(func, func.getLevel(), list);
        return list;
    }

    private List<Integer> getRecurFather(SamlType t, int level, List<Integer> list) {
        list.add(t.getId());
        int fatherid = t.getFather();
        if (fatherid == 0) {
            if (level + 1 == Data.SUBSYS) {
                String err = Data.OK + ":" + t.getName() + "的父类型是根目录/";
                //System.out.println(err);
                return list;
            } else {
                String err = Data.ErrorsByLevel[level + 1] + ":" + t.getName() + "的父类型不存在";
                System.out.println(err);
                return null;
            }
        } else {
            SamlType father = samlTypeDao.findSamlTypeById(fatherid);
            return getRecurFather(father, level + 1, list);
        }
    }


    /**
     * 获取一个函数一直到子系统的所有类型列表，如果能到根就返回，否则返回null。
     *
     * 两种方法：
     * 1、老老实实递归获取：getRecurFather
     * 2、直接split当前type的path，然后存起来名字。再去逐个查type里有没有。
     *
     */
    @Override
    public List<SamlType> getFuncRecurFathers(SamlType func, List<ErrorClassify> errList) {
        if (func.getLevel() != 2) {
            System.out.println(func.getName() + "不是函数！");
            return null;
        }
        List<SamlType> list = new ArrayList<>();

        //方法1：
        list = getRecurFather(func, func.getLevel(), list, errList);
        return list;


        //方法2：【未完，不想写了】2022年3月7日21:05:48
        //String path = func.getPath();
        //int len = path.length();
        //String endChars = path.substring(len - 2, len);
        //if (endChars.equals(".h") || endChars.equals(".c")) {//文件，func是全局函数
        //    int index1 = path.indexOf("/");
        //    int index2 = path.lastIndexOf("/");
        //    String subsys = path.substring(index1);
        //    String dentry = path.substring(index1 + 1, index2);
        //    String file = path.substring(index2 + 1, len);
        //
        //} else {// 结构体，func是成员函数
        //
        //}
    }

    /**
     * 递归获取所有父类列表【包括自己】
     * 如果无法到达子系统subsys层就没有父亲了，就返回null。
     * 否则，返回整棵树上的类型。
     */
    private List<SamlType> getRecurFather(SamlType type, int level, List<
            SamlType> list, List<ErrorClassify> errList) {
        list.add(type);
        SamlType father = samlTypeDao.findSamlTypeById(type.getFather());
        if (father == null) {
            if (level + 1 == Data.SUBSYS) {
                String err = Data.OK + ":" + type.getName() + "的父类型是根目录/";
                //System.out.println(err);
                errList.add(new ErrorClassify(Data.OK, err));
                return list;
            } else {
                String err = Data.ErrorsByLevel[level + 1] + ":" + type.getName() + "的父类型不存在";
                System.out.println(err);
                errList.add(new ErrorClassify(Data.ErrorsByLevel[level + 1], err));
                return null;
            }
        } else {
            return getRecurFather(father, level + 1, list, errList);
        }
    }

    /**
     * 输入调用关系表达式exp，返回两个FuncId
     */
    @Override
    public int[] getFuncIds(String invocExp) {
        String[] funcsName = invocExp.split(";");
        String fun1Name = funcsName[0];
        String fun2Name = funcsName[1];
        List<SamlType> func1 = samlTypeDao.findByNameAndLevel(fun1Name, Data.FUNC);
        int func1Id = 0, func2Id = 0;
        if (func1 != null && func1.size() != 0) {
            func1Id = func1.get(0).getId();
        }
        List<SamlType> func2 = samlTypeDao.findByNameAndLevel(fun2Name, Data.FUNC);
        if (func2 != null && func2.size() != 0) {
            func2Id = func2.get(0).getId();
        }
        int[] funcIds = {func1Id, func2Id};

        return funcIds;
    }


    /**
     * 判断类型是否存在，若存在，返回相应的type_id，否则返回0；
     * 由于前面已经把exp中的alias替换为original，因此这里判断存在性就不用查alias表了。
     *
     * 考虑alias表【暂时不用，注释掉】
     * 1、看saml_type中是否有该type_name，
     *      (1)若有直接返回id；
     *      (2)若没有，从saml_type_alias表中查本质original_type，
     *          若有，再看saml_type中是否有这个original_type；
     *              若有返回id；
     *              若无返回0；
     *          若无，说明alias中也没有，直接返回0；
     *
     * */
    @Override
    public int typeExist(String type_name) {
        List<SamlType> list1 = samlTypeDao.findByName(type_name);
        if (list1 == null || list1.size() == 0) {
            String err = "当前type不存在：" + type_name;
            System.out.println(err);
            String cl = Data.LackError;
            int fatherId = 0;
            List<TypeLack> exist = typeLackDao.findByNameAndErrClassifyAndFatherId(type_name, cl, fatherId);
            if (!exist.isEmpty()) {
                typeLackDao.updateById(exist.get(0).getId(), "SamlType", fatherId, type_name, err, cl);
            } else {
                typeLackDao.insert("SamlType", fatherId, type_name, err, cl);
            }
            return 0;
        } else {
            return list1.get(0).getId();
        }
    }

    /**
     * 合并两个list，得保证自己不重复，还要两个合并也不重复。
     *
     * 这里的相等，依赖于SamlType重写的equals方法！！！
     */
    @Override
    public List<Integer> mergeNoRepeat(List<Integer> list1, List<Integer> list2) {
        List<Integer> mergeList = new ArrayList<>();

        if (list1 != null && !list1.isEmpty()) {
            for (Integer s : list1) {
                if (!mergeList.contains(s)) {
                    mergeList.add(s);
                }
            }
        }

        if (list2 != null && !list2.isEmpty()) {
            for (Integer s : list2) {
                if (!mergeList.contains(s)) {
                    mergeList.add(s);
                }
            }
        }
        return mergeList;
    }

    public List<SamlType> mergeNoRepeat1(List<SamlType> list1, List<SamlType> list2) {
        List<SamlType> mergeList = new ArrayList<>();

        if (list1 != null) {
            for (SamlType s : list1) {
                if (!mergeList.contains(s)) {
                    mergeList.add(s);
                }
            }
        }

        if (list2 != null) {
            for (SamlType s : list2) {
                if (!mergeList.contains(s)) {
                    mergeList.add(s);
                }
            }
        }
        return mergeList;
    }

    /**
     * 根据id和层次，判断该类型是否在type表中存在。
     */
    @Override
    public boolean typeExist(int typeId, int level) {
        SamlType type = samlTypeDao.findSamlTypeById(typeId);
        if (type != null && type.getLevel() == level) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据字符串返回类型列表，字符串都是类型Id
     */
    @Override
    public List<SamlType> getMembersFromList(String ids) {
        List<SamlType> list = new ArrayList<>();
        String[] strs = ids.split(",");
        for (String s : strs) {
            int id = Integer.parseInt(s);
            SamlType t = samlTypeDao.findSamlTypeById(id);
            if (t == null) {
                System.out.println("类型不存在");
            } else {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 输入格式：【level|path|exp】就需要这三个字段
     * 全部存储到type表中。
     * include单独存储到
     *
     * filePath是根目录，无斜杠。
     */
    @Override
    public void typeSave(List<String> list, String filePath, int pro) {
        for (String s : list) {
            String[] strs = s.split("\\|");
            System.out.println(s);
            int level = Integer.parseInt(strs[0]);
            filePath = filePath.replace("\\", "/");
            String path = strs[1].replace("\\", "/");
            path = path.replace(filePath + "/", "");
            String exp = strs[2].replace("\\", "/");
            exp = exp.replace(filePath + "/", "");


            //生成name
            String name = "";
            if (level == Data.FUNC || level == Data.STRUCT || level == Data.FUNC_DECLARE || level == Data.STRUCT_DECLARE) {
                if (exp.contains("=")) {// 把那些赋值的结构体变量弄出来
                    level = Data.VARS;
                } else {
                    name = StrUtil.splitName(exp, level);
                }
            }
            //过滤掉看起来像函数，实际上没有返回值的宏
            // eg: static DEFINE_PER_CPU(unsigned long, nr_inodes);
            if (level == Data.FUNC || level == Data.FUNC_DECLARE) {
                String returnType = StrUtil.funcIgnoreWithStar(exp.substring(0, exp.indexOf("("))).replace(name, "").trim();
                System.out.println("hhhhhhhhhhhhhh" + exp + "hhhhhhhhhhhhhh");
                System.out.println("hhhhhhhhhhhhhh" + returnType + "hhhhhhhhhhhhhh");
                if (returnType.equals("")) {
                    level = Data.MACRO;
                }
                System.out.println(level);
                System.out.println("hhhhhhhhhhhhhh" + returnType.equals("") + "hhhhhhhhhhhhhh");
            }


            // static标记
            if (level == Data.INCLUDE || level == Data.FUNC_DECLARE || level == Data.STRUCT_DECLARE) {
                List<IncludeImport> tmp = includeImportDao.findByLevelAndPathAndExpAndPro(level, path, exp, pro);
                if (tmp == null || tmp.isEmpty()) {
                    IncludeImport inc;
                    if (exp.startsWith("static") && !exp.startsWith("static inline")) {
                        inc = new IncludeImport(level, path, name, exp, 1, pro);
                    } else {
                        inc = new IncludeImport(level, path, name, exp, 0, pro);
                    }
                    includeImportDao.save(inc);
                }
            } else if (level == Data.VARS || level == Data.MACRO) {
                List<SamlTypeMacro> tmp = samlTypeMacroDao.findByLevelAndPathAndExpAndProject(level, path, exp, pro);
                if (tmp == null || tmp.isEmpty()) {
                    SamlTypeMacro t;
                    if (exp.startsWith("static") && !exp.startsWith("static inline")) {
                        t = new SamlTypeMacro(level, name, path, exp, 0, pro, 1);
                    } else {
                        t = new SamlTypeMacro(level, name, path, exp, 0, pro, 0);
                    }
                    samlTypeMacroDao.save(t);
                }
            } else {
                List<SamlType> tmp = samlTypeDao.findByLevelAndNameAndPathAndProject(level, name, path, pro);
                if (tmp == null || tmp.isEmpty()) { // 没有再插入，不空不插入
                    SamlType t;
                    if (exp.startsWith("static") && !exp.startsWith("static inline")) {
                        t = new SamlType(level, name, path, exp, 0, pro, 1);
                    } else {
                        t = new SamlType(level, name, path, exp, 0, pro, 0);
                    }
                    samlTypeDao.save(t);
                } else if (tmp.size() == 1) {
                    int id = tmp.get(0).getId();
                    samlTypeDao.updateExp(id, exp);
                    if (exp.startsWith("static") && !exp.startsWith("static inline")) {
                        samlTypeDao.updateStatic(id, 1);
                    } else {
                        samlTypeDao.updateStatic(id, 0);
                    }
                } else {
                    System.out.println("有重复，应该不会！");
                }
            }

        }
    }

}
