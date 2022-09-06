package com.example.dynamic_validate.service.imp;

import com.example.dynamic_validate.dao.SamlListDao;
import com.example.dynamic_validate.dao.SamlRelationDao;
import com.example.dynamic_validate.dao.SamlTypeDao;
import com.example.dynamic_validate.dao.TypeLackDao;
import com.example.dynamic_validate.data.Data;
import com.example.dynamic_validate.entity.SamlList;
import com.example.dynamic_validate.entity.SamlType;
import com.example.dynamic_validate.entity.TypeLack;
import com.example.dynamic_validate.service.GenerateRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 关系生成、关系检查算法
 */
@Service
public class GenerateRelationServiceImp implements GenerateRelationService {
    @Value("${finish.type.fatherRelation.fromFirstNotdeal}")
    private boolean fromFirstNotdeal;

    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private SamlListDao samlListDao;
    @Autowired
    private SamlRelationDao samlRelationDao;
    @Autowired
    private TypeLackDao typeLackDao;

    /**
     * 1.子类型关系
     *
     * level4-7
     * 组件之间怎么会有子类型呢？
     *
     * level3
     * 类实现接口属于子类型。【X】
     * 子类继承父类也属于子类型。【X】
     * 接口继承接口应该也是子类型吧。【X】
     * 反正子类型就是，就是约束变多，范围变小。【可替换！】
     *
     *
     * level2
     * 方法之间的子类型又怎么定义？我的天哪！
     *
     * level1
     * 基本类型的子类型关系就是int小于float小于double。
     */
    @Override
    public void generateSubType() {
        //level=3

    }

    /**
     * 2.聚合关系【依赖建模】
     * 两个来源：
     * ①在人工建模的时候直接存储relation和type。【目前没有前台，暂无】
     * ②从type中产生聚合关系：
     * level5~6，(Aggregation,当前type_id,list1,1)
     *
     * level4，文件组成关系，(FuncFile,当前file_id,list1,1), (StructFile,当前type_id,list2,1)
     * level3，成员关联关系，(VarStruct,当前type_id,list1,1)，(FuncStruct,当前type_id,list2,1)
     * level2，参数关联关系，(ParamIn,当前type_id,list1,1)，(ParamOut,当前type_id,list2,1)
     * level1，Base类型，没有聚合关系。
     *
     *
     * 2022年5月8日 06:22:46：把所有的关系变成一对一的关系！
     *
     */
    @Override
    public void generateAggregation() {
        for (int i = Data.DENTRY; i <= Data.SUBSYS; i++) {
            List<SamlType> level_type;
            if (fromFirstNotdeal) {
                int typeId = samlRelationDao.findLastTypeId();
                level_type = samlTypeDao.findByLevelAndState(i, Data.State, typeId);
            } else {
                level_type = samlTypeDao.findByLevelAndState(i, Data.State);//当前type，其实是父亲，直接存储孩子就行了
            }

            for (SamlType type : level_type) {
                if (type.getList1() != 0) {
                    saveBatchRelation(type.getId(), type.getList1(), Data.Aggregation);
                }
                if (type.getList2() != 0) {
                    saveBatchRelation(type.getId(), type.getList2(), Data.Aggregation);
                }
            }
        }
    }

    @Deprecated
    public void generateAggregation1() {
        //自底向上找！
        //level=4~6，都有孩子
        for (int i = Data.DENTRY; i <= Data.SUBSYS; i++) {
            List<SamlType> level_type = samlTypeDao.findByLevelAndState(i, Data.State);//当前type，其实是父亲，直接存储孩子就行了
            for (SamlType type : level_type) {
                int list1Id = type.getList1();
                samlRelationDao.insertIgnore(Data.Aggregation, type.getId(), list1Id, Data.field2List);
                // {Aggregation,father的typeId,sub的listId,field2是list类型}
                String members = samlListDao.findSamlListById(list1Id).getMemberType();
                System.out.println("sub:{" + members + "}，father:" + type.getId() + "，聚合关系");
            }
        }
    }

    /**
     * 3.类关联【区分类和接口】
     * TypeAssociation
     *
     * 还是从saml_type中生成。
     * 聚合：一个父亲多个儿子，{Agg,father,sub_list = {sub1,sub2,sub3}，0，1}，注意顺序是父亲在前
     * 类关联：一个儿子多个父亲,{TypeAss,sub,father_list = {father1,father2,father3}，0，1}，注意顺序是儿子在前
     * 其实真正的类关联，是father_list的任意两两组合，都是类关联！
     *
     * 在for循环结束，其实也就是不用lower的时候，直接把upper赋值给lower，可以少查一次数据库，好棒！
     *
     *
     * 合并TypeAssociation和Aggregation为一个方法，操作如下：
     * //改名为：generateAggAndTypeAss()
     * //这个是顺便可以生成聚合关系，但是有个问题，就是外层判断size>=2，导致这里的聚合关系不完整，所以干脆还是在另外的“聚合”方法里写吧。
     * //如果relation中的“list拆开存储”，那就“聚合和类关联关系”合并，需要把size>=2去掉，并且还要i从4-7，因为这个方法更有优势！！！
     */
    @Override
    @Deprecated
    public void generateTypeAssociation() {
        //saml_type表
        //自底向上找！
        //level=7，顶层，不可能有类关联关系。无需生成。
        //level=4~6，以上层为基准是4~6，如果以下层为基准，应该是3~5。
        List<SamlType> lowerType = samlTypeDao.findByLevelAndState(Data.STRUCT, Data.State);//下层type，先查第一个，之后的就直接把upper的结果拿过来就好。
        List<SamlType> upperType = null;
        for (int i = 4; i <= 6; i++) {
            upperType = samlTypeDao.findByLevelAndState(i, Data.State);//以上层为基准，作为上层type

            if (upperType.size() >= 2) {

                for (SamlType lower : lowerType) { //外层是下层，遍历每一个下层，找他们对应的上层
                    StringBuilder str = new StringBuilder("");
                    int count = 0;

                    for (SamlType upper : upperType) {

                        SamlList samlList = samlListDao.findById(upper.getList1()).get();

                        List<String> memberTypeId = Arrays.asList(samlList.getMemberType().split(","));

                        System.out.println();
                        System.out.println("?????????????????");
                        System.out.println(lower.getId());
                        System.out.println(memberTypeId);
                        System.out.println("?????????????????");
                        System.out.println();

                        if (memberTypeId.contains("" + lower.getId())) {
                            //这个是顺便可以生成聚合关系，但是有个问题，就是外层判断size>=2，导致这里的聚合关系不完整，所以干脆还是在另外的“聚合”方法里写吧。
                            // 如果relation中的“list拆开存储”，那就“聚合和类关联关系”合并，需要把size>=2去掉，并且还要i从4-7，因为这个方法更有优势！！！
                            System.out.println("sub的typeId:" + lower.getId() + "，father:" + upper.getId() + "，有关系，聚合关系");
                            // samlRelationDao.save(Data.SubType, upper.getId(), samlList.getId(), 1);

                            str.append(upper.getId()).append(",");
                            count++;
                        }

                    }

                    if (count >= 2) { //只有当父亲>=2个的时候，才有意义，否则，没有“类型关联关系”，不用存储。
                        System.out.println("============================");
                        System.out.println(str);
                        System.out.println("============================");

                        //把最后多出来的逗号删掉【其实不删除可能更好，如果往里添加元素更好添加，但是后期可能在字符串分割时有空字符串的隐患，还是删了吧】？？
                        //String fatherListMembers = str.substring(0, str.length() - 1);

                        //先存list
                        int memberClassify = 2;//relation中的list
                        //typeCompound默认0，不用传参了
                        String remark = "relation中的field2是list：类型TypeAssociation，存储fathers";
                        int fatherListId = 0;
                        // 【这个方法用不了，因为member_type要变成够长的longtext，就不能作为索引了。反正也不用生成类关联关系了，也无所谓了】
                        //int fatherListId = samlListDao.insertIgnore(fatherListMembers, Data.field2Type, remark);

                        //再存relation
                        //field3默认0，不用设置了。
                        //一个儿子多个父亲,{TypeAss,sub,father_list = {father1,father2,father3}，0，1}
                        samlRelationDao.insertIgnore(Data.TypeAssociation, lower.getId(), fatherListId, Data.field2List);
                    }

                }
            }
            lowerType = upperType; //在for循环结束，其实也就是不再用lower的时候，直接把upper赋值给lower，可以少查一次，好棒！
        }

        // lowerType = samlTypeDao.findByLevelAndProject(i);//下层type，先查第一个，之后的就直接把upper的结果拿过来就好。
        // lowerType = upperType; //在for循环结束，其实也就是不再用lower的时候，直接把upper赋值给lower，可以少查一次，好棒！
    }

    /**
     * 4-5.参数关联【依赖建模】
     *
     * level2
     * 输入类型或输出类型，和方法之间的关系。
     *
     * 来源：
     * ①建模的时候，直接存到relation和type中。
     * ②从type表中生成：
     * level2，这个是，参数关联关系，(ParamInAssociation,当前type_id,list1,1)，(ParamOutAssociation,当前type_id,list2,1)
     *
     * 应该存储：
     * (ParamIn, 当前f的type_id, field1第i个成员type_id,0)
     * (ParamOut, 当前f的type_id, field2的唯一成员的type_id,0)
     */
    @Deprecated
    public void generateParaAssociation1() {
        List<SamlType> typeLevel2 = samlTypeDao.findByLevelAndState(Data.FUNC, Data.State);
        for (SamlType type : typeLevel2) {
            int inTypeList = type.getList1();//saml_list1=in_list
            int returnType = type.getList2();//saml_list2=out_list

            System.out.println("方法的id:" + type.getId() + "，方法的输入Type的list的id:" + inTypeList + "，方法的输出Type的list的id:" + returnType + "，有关系，参数关联关系");
            samlRelationDao.insertIgnore(Data.ParamIn, type.getId(), inTypeList, 1);
            samlRelationDao.insertIgnore(Data.ParamOut, type.getId(), returnType, 1);
            // {Function，接口type的id，接口包含方法type的list的id，field2是list类型}
        }
    }

    /**
     * 4-5.参数关联【依赖建模】
     *
     * level2
     * 输入类型或输出类型，和方法之间的关系。
     *
     * 来源：
     * ①建模的时候，直接存到relation和type中。
     * ②从type表中生成：
     * (ParamIn, 当前f的type_id, field1第i个成员type_id,0)
     * (ParamOut, 当前f的type_id, field2的唯一成员的type_id,0)
     */
    @Override
    public void generateParaAssociation() {
        List<SamlType> fs;
        if (fromFirstNotdeal) {
            int typeId = samlRelationDao.findLastTypeId();
            fs = samlTypeDao.findByLevelAndState(Data.FUNC, Data.State, typeId);
        } else {
            fs = samlTypeDao.findByLevelAndState(Data.FUNC, Data.State);
        }

        for (SamlType f : fs) {
            int inTypeList = f.getList1();//saml_list1=in_list
            int outType = f.getList2();//saml_list2=out_list

            //System.out.println("方法的id:" + f.getId() + "，方法的输入Type的list的id:" + inTypeList + "，方法的输出Type的id:" + outTypeList + "，有关系，ListAgg关系");
            //samlRelationDao.insertIgnore(Data.ListAgg, f.getId(), inTypeList, Data.field2List);
            //samlRelationDao.insertIgnore(Data.ListAgg, f.getId(), outTypeList, Data.field2List);

            if (inTypeList != 0) {
                saveBatchRelation(f.getId(), inTypeList, Data.ParamIn);
            }
            if (outType == 0) {
                String name = f.getList2Exp();
                String err = "没有输出参数，不应该的，报错！【输出参数类型缺失】" + name;
                System.out.println(err);
                int fid = f.getId();
                String cl = Data.FuncReturnLackError;
                List<TypeLack> exist = typeLackDao.findByNameAndErrClassifyAndFatherId(name, cl, fid);
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), "SamlType", fid, name, err, cl);
                } else {
                    typeLackDao.insert("SamlType", fid, name, err, cl);
                }
            } else {
                samlRelationDao.insertIgnore(Data.ParamOut, f.getId(), outType, Data.field2Type);
            }

            //int out_type = Integer.parseInt(samlListDao.findSamlListById(outTypeList).getMemberType());
            // {Function，接口type的id，接口包含方法type的list的id，field2是list类型}
        }
    }

    /**
     * 6-7.成员变量+函数关联【依赖建模】
     * level3
     * 成员-结构体之间的对应关系，包括成员变量类型-结构体和成员函数-结构体。
     *
     * 两种来源：
     * ①人给结构【代码级，需要一个数据结构？】，直接在建模的时候存储，同时存到type和relation两个表中【或者，先存到type，再生成也凑合吧，不过，最好直接存！省算力】
     * ②从saml_type表中生成，level=3。
     * level3，这个是，成员关联关系，(VarStruct,当前type_id,list1,1)，(FuncStruct,当前type_id,list2,1)
     *
     * 2022年1月18日22:43:24  更改：只把var的生成变成1-1关系，function暂时不动。
     *      调用方法 saveBatchRelation
     *
     * 2022年3月26日21:35:51  更改：var和function都存储1-1关系。
     *
     * 2022年4月13日13:05:29  更改：1-n和1-1两种关系都存
     */
    @Override
    public void generateVarFunction() {
        List<SamlType> typeLevel3;
        if (fromFirstNotdeal) {
            int typeId = samlRelationDao.findLastTypeId();
            typeLevel3 = samlTypeDao.findByLevelAndState(Data.STRUCT, Data.State, typeId);
        } else {
            typeLevel3 = samlTypeDao.findByLevelAndState(Data.STRUCT, Data.State);
        }

        for (SamlType type : typeLevel3) {
            int varList = type.getList1();//list1是属性
            int funcList = type.getList2();//list2是方法

            //System.out.println("结构体的id:" + type.getId() + "，成员变量list的id:" + varList + "，成员变量关联关系");
            //System.out.println("结构体的id:" + type.getId() + "，成员函数list的id:" + funcList + "，成员函数关联关系");
            //samlRelationDao.insertIgnore(Data.ListAgg, type.getId(), varList, Data.field2List);
            //samlRelationDao.insertIgnore(Data.ListAgg, type.getId(), funcList, Data.field2List);
            if (varList != 0) {
                saveBatchRelation(type.getId(), varList, Data.VarStruct);
            }
            if (funcList != 0) {
                saveBatchRelation(type.getId(), funcList, Data.FuncStruct);
            }
        }
    }

    /**
     * 8.可访问
     *
     *作用域可调用关系，应该是根据作用域产生。但是，可能太多了，不好存储，干脆就不存了。
     *
     * 只需要在验证的时候判断一下好了。
     */
    @Override
    public void generateAccessible() {

    }

    /**
     * 9.函数可调用
     *
     * 9.1成员变量关联【依赖建模】
     * 9.2接口类型可调用【依赖成员变量关联】
     *
     * 产生成员变量关联关系：成员变量关联+接口、组件等类型可调用（递归）
     */
    @Override
    public void generateInvocable() {

    }

    /**
     * 10.传参可调用【依赖子类型】
     *
     */
    @Override
    public void generateParamInvocable() {

    }

    /**
     * 11,12.函数文件关联，结构体文件关联
     * 全局函数与文件的关系，1-1关系
     * (FuncFile,file,function,0)
     *
     * 2022年4月13日13:05:29  更改：1-n和1-1两种关系都存
     */
    @Override
    public void generateFuncStructFile() {
        List<SamlType> files;
        if (fromFirstNotdeal) {
            int typeId = samlRelationDao.findLastTypeId();
            files = samlTypeDao.findByLevelAndState(Data.FILE, Data.State, typeId);
        } else {
            files = samlTypeDao.findByLevelAndState(Data.FILE, Data.State);
        }

        //SamlType nameic = samlTypeDao.findSamlTypeById(28865);
        //files = new ArrayList<>();
        //files.add(nameic);

        for (SamlType type : files) {
            int list1Id = type.getList1();
            int list2Id = type.getList2();
            if (list1Id == 0 && list2Id == 0) {
                System.out.println("这个文件是空的，什么都没有：" + type.getName());
            }
            //System.out.println("文件的id:" + type.getId() + "，全局函数list的id:" + list1Id + "，函数文件关联关系");
            //samlRelationDao.insertIgnore(Data.ListAgg, type.getId(), list1Id, Data.field2List);
            //samlRelationDao.insertIgnore(Data.ListAgg, type.getId(), list2Id, Data.field2List);

            if (list1Id != 0) {
                saveBatchRelation(type.getId(), list1Id, Data.FuncFile);
            }
            if (list2Id != 0) {
                saveBatchRelation(type.getId(), list2Id, Data.StructFile);
            }
        }
    }

    /**
     * 问题：
     * 存储list中的member_type和member_list，但是，都是VarStruct的情况下如何区分二者呀？
     * 用field2Classify区分：member_type和member_list都存上，用field2Classify区分，前者为0，后者为1。
     *
     * 功能：批量保存【ParamIn/VarStruct/FuncStruct/FuncFile/StructFile】关系到saml_relation表中，1-1关系
     * 输入：父类型type和其所有成员的type_id
     *
     * 保存List的成员，即T-T关系，如果List成员不空的话, 就是List-T关系
     */
    private void saveBatchRelation(int tid, int varList, int reClassify) {
        SamlList list = samlListDao.findSamlListById(varList);
        if (list == null) {
            System.out.println("出错啦，列表为空，无法存储！！");
            return;
        }
        String mt = list.getMemberType();
        if (mt != null && !mt.equals("")) {// 这里到底是null还是""？ null应该是
            String[] memberType = mt.split(",");
            for (String s : memberType) {
                if (!s.equals("0") && !s.equals("")) {
                    int varId = Integer.parseInt(s);
                    samlRelationDao.insertIgnore(reClassify, tid, varId, Data.field2Type);
                }
            }
        }
        String ml = list.getMemberList();
        if (ml != null && !ml.equals("")) {// 这里到底是null还是""？
            String[] memberList = ml.split(",");
            for (String s : memberList) {
                if (!s.equals("0")) {
                    int varId = Integer.parseInt(s);
                    samlRelationDao.insertIgnore(reClassify, tid, varId, Data.field2List);
                }
            }
        }
    }

}
