package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.AliasDao;
import com.dynamic_validate.dao.DemandInvokeDao;
import com.dynamic_validate.dao.SamlListDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.Alias;
import com.dynamic_validate.entity.SamlList;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.GenerateListService;
import com.dynamic_validate.service.SamlListService;
import com.dynamic_validate.service.SamlTypeService;
import com.dynamic_validate.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GenerateListServiceImp implements GenerateListService {
    @Value("${lamvs.project}")
    private int pro;

    public static final int POINTER_FUNC = 1; //指针类型的函数，在struct声明中，.h文件中？
    public static final int GLOBAL_FUNC = 2; //全局函数，在实现中后面直接接"{" 或 声明中后面接";"，在.c或.h文件中？
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private SamlListDao samlListDao;
    @Autowired
    private AliasDao aliasDao;
    @Autowired
    private DemandInvokeDao demandInvokeDao;
    @Autowired
    private SamlListService samlListService;
    @Autowired
    private SamlTypeService samlTypeService;

    /**
     * 生成：[name,list1,list2] 部分内部定义有father
     *
     * 根据type表，生成需要的list1和list2：从表达式中产生list1和list2，存到list中
     * 完善list1和list2字段：并把list_id存到type字段中。
     */
    @Override
    public void generListByExp(int pro) {
        generAllStructList(pro); // 可能产生内部结构体，成员函数

        generAllFuncList(pro); //可能产生新函数，函数参数
        //generAllFuncDeclareList(pro); //可能产生新函数，函数参数【没必要解析，因为和原函数一毛一样，找到原函数才是重点！】

        generAllFileList(pro); //exp需要前面Struct和Func生成的name
        generateLevel4_6ListByExp(pro);

    }

    /**
     * 这个是以List全部设置为Valid2为前提的。
     */
    @Override
    public void listDistinct() {
        System.out.println("===开始：去重===");
        // 表达式等去重，最好把对应的type中的exp也一并更新了。
        List<SamlList> lists = samlListDao.findAllValid2();
        for (SamlList list : lists) {
            int lid = list.getId();
            String exp = StrUtil.distinct(list.getExp());
            String mt = StrUtil.distinct(list.getMemberType());
            System.out.println(lid + ":Exp  old:" + list.getExp() + ", new:" + exp);
            System.out.println(lid + ":Ids  old:" + list.getMemberType() + ", new:" + mt);
            if (!exp.equals(list.getExp())) { // 不相等时候再更新，否则不用更新
                samlListDao.updateById(lid, mt, exp); // 更新list
                samlTypeDao.updateList1exp(lid, exp);// 更新type中的表达式exp1
                samlTypeDao.updateList2exp(lid, exp);// 更新type中的表达式exp2
            }
        }

        //处理valid0的表达式，去重【有必要吗？暂时没必要吧】
        System.out.println("===结束：去重===");

    }

    private void generAllFileList(int pro) {
        //List<String> allFilePath = samlTypeDao.getAllFilePath(pro);
        List<SamlType> allFilePath = samlTypeDao.findByLevelAndProject(Data.FILE, pro);
        for (SamlType t : allFilePath) {
            String path = t.getPath() + "/" + t.getName();
            Map<String, String> list1 = samlTypeDao.getListByLevel(path, Data.FUNC, pro);
            Map<String, String> list2 = samlTypeDao.getListByLevel(path, Data.STRUCT, pro);
            int listCompound = 8;
            String list1exp = list1.get("exp");
            String list2exp = list2.get("exp");

            if (list1exp == null || list1exp.length() == 0) {
                list1exp = "";
            } else {
                list1exp = StrUtil.distinct(list1exp); // 去重
            }
            if (list2exp == null || list2exp.length() == 0) {
                list2exp = "";
            } else {
                list2exp = StrUtil.distinct(list2exp);
            }

            int list1_id = samlListService.insertUpdateListId(list1exp, list1.get("ids"), listCompound);
            int list2_id = samlListService.insertUpdateListId(list2exp, list2.get("ids"), listCompound);
            samlTypeDao.updateList(t.getId(), list1_id, list2_id);
            samlTypeDao.updateListExp(t.getId(), list1exp, list2exp); //费半天劲解析的两个表达式，不存实在是太可惜啦
        }
    }

    /**
     * 从exp中产生level4~level6的list，也就是聚合类型的list。
     */
    private void generateLevel4_6ListByExp(int pro) {
        for (int i = Data.FILE; i <= Data.SUBSYS; i++) {
            List<SamlType> typeList = samlTypeDao.findByLevelAndStateAndProject(i, Data.State, pro);
            if (typeList == null || typeList.size() == 0) {
                continue;
            }
            for (int j = 0; j < typeList.size(); j++) {
                SamlType curType = typeList.get(j);
                String exp = curType.getExp();
                if (exp != null && exp.replaceAll("\\s*", "").length() > 0) {
                    //1、替换alias为original
                    exp = alias2OriginalType(exp);
                    //2、直接对exp进行Ignore处理，避免下面瞎操作了！
                    exp = StrUtil.funcIgnoreWithStar(exp);
                    //3、删掉表达式两头的大括号
                    exp = exp.trim().replaceAll("\\{|\\}", "");
                    System.out.println(exp);

                    //更新两个list
                    //更新list1和list2，file列表和dentry列表
                    int listCompound = 1;
                    int list1_id = 0;
                    int list2_id = 0;

                    String exp1 = exp.substring(0, exp.indexOf(";")); // 去重
                    String exp2 = exp.substring(exp.indexOf(";"));

                    if (!exp1.equals("")) {
                        exp1 = StrUtil.distinct(exp1);
                        list1_id = samlListService.insertUpdateListId(exp1, listCompound);
                    }
                    if (!exp2.equals(";")) {
                        exp2 = exp2.replace(";", "");
                        exp2 = StrUtil.distinct(exp2);
                        list2_id = samlListService.insertUpdateListId(exp2, listCompound);
                    }
                    System.out.println(list1_id + "++++++");
                    samlTypeDao.updateList(curType.getId(), list1_id, list2_id);
                    samlTypeDao.updateListExp(curType.getId(), exp1, exp2); //费半天劲解析的两个表达式，不存实在是太可惜啦
                }
            }
        }
    }

    /**
     * level3：从struct的exp中产生list1和list2，存到type表中。
     * list1是变量集合，list2是函数集合，list1内部和list2内部都是multi。
     */
    private void generAllStructList(int pro) {
        List<SamlType> structs = samlTypeDao.findByLevelAndStateAndProject(Data.STRUCT, Data.State, pro);
        if (structs == null || structs.size() == 0) {
            return;
        }
        for (int i = 0; i < structs.size(); i++) {
            SamlType curStruct = structs.get(i);
            System.out.println("=====ididididididid" + curStruct.getId() + ":" + curStruct.getExp());
            generStructListByExp(curStruct);
        }
    }

    /**
     * 解析一个结构体
     * 1、parse，得到[name,list1,list2]
     * 2、更新 name
     * 3、存储list1和list2
     */
    @Override
    public void generStructListByExp(SamlType curStruct) {
        // 1、parse，得到[name,list1,list2]
        String[] struct_split_exp = parseStructByExp(curStruct);
        if (struct_split_exp == null) {
            System.out.println(curStruct.getId() + ":" + curStruct.getName() + "，exp不符合规范！");
            return;
        }
        System.out.println("===========" + struct_split_exp[0] + "==========");
        System.out.println("===========" + struct_split_exp[1] + "==========");
        System.out.println("===========" + struct_split_exp[2] + "==========");


        //2、更新结构体名【这是最开始，本来就都没有名字，也不用判断了。】
        samlTypeDao.updateName(curStruct.getId(), struct_split_exp[0]);

        //3、更新两个list
        String var_list = struct_split_exp[1];
        if (!var_list.equals("")) {
            var_list = StrUtil.distinct(var_list);// 去重
        }
        String func_list = struct_split_exp[2];
        if (!func_list.equals("")) {
            func_list = StrUtil.distinct(func_list);
        }

        //更新list1即var_list，更新list2即func_list。两个list需要判断list表中是否存在，得到list_id，然后插入到type表。
        int compound_vars = 6, compound_func = 7;
        int list1_id = samlListService.insertUpdateListId(var_list, compound_vars);
        int list2_id = samlListService.insertUpdateListId(func_list, compound_func);
        System.out.println(list1_id + "++++++" + list2_id);
        samlTypeDao.updateListExp(curStruct.getId(), var_list, func_list); //费半天劲解析的两个表达式，不存实在是太可惜啦
        samlTypeDao.updateList(curStruct.getId(), list1_id, list2_id);
    }

    /**
     * 从结构体exp解析出成员函数和变量。全部替换成ID了。
     *
     * 1、获取name,body
     * 2、处理结构体body内部定义的union或enum或struct，替换成ID
     * 3、从body解析出list1和list2
     *      保存成员函数
     *      递归生成成员函数的List【这一步不要干了，没必要，因为结构体后有对全部函数的处理】
     * 4、返回[name,list1,list2]
     *
     * 格式：
     * struct modversion_info {unsigned long crc; char name[MODULE_NAME_LEN]; };
     * struct module_kobject {struct kobject kobj; struct module *mod; struct kobject *drivers_dir; struct module_param_attrs *mp; struct completion *kobj_completion; };
     * struct module_attribute {struct attribute attr; ssize_t (*show)(struct module_attribute *, struct module_kobject *,char *); ssize_t (*store)(struct module_attribute *, struct module_kobject *,const char *, size_t count); void (*setup)(struct module *, const char *); int (*test)(struct module *); void (*free)(struct module *); };
     * struct file {union { struct llist_node fu_llist; struct rcu_head  fu_rcuhead; };struct path  f_path; struct inode  *f_inode; const struct file_operations *f_op; spinlock_t  f_lock; enum rw_hint  f_write_hint; atomic_long_t  f_count; unsigned int   f_flags; fmode_t   f_mode; struct mutex  f_pos_lock; loff_t   f_pos; struct fown_struct f_owner; const struct cred *f_cred; struct file_ra_state f_ra; u64   f_version; void   *f_security; void   *private_data; struct list_head f_ep_links; struct list_head f_tfile_llink; struct address_space *f_mapping; errseq_t  f_wb_err; };
     *
     * 全是成员变量
     * 成员有union或enum或struct
     * 全是成员函数
     * 掺和着
     *
     * 1、看是否包含union或enum或struct【如果是匿名怎么处理？】
     *      如果没有这些定义，直接用分号隔开完全没问题。
     *      如果有这些定义，需要先匹配出来，替换掉再说。【定义的标志是关键字+{}】
     *
     * eg:
     *      struct file {union { struct llist_node fu_llist; struct rcu_head  fu_rcuhead; };struct path  f_path;};
     *
     * 2、看是否包含函数指针。【标志是()()，Data.paramFuncReg】
     *
     */
    @Override
    public String[] parseStructByExp(SamlType curStruct) {
        int id = curStruct.getId();
        String path = curStruct.getPath();
        String exp = curStruct.getExp();
        exp = alias2OriginalType(exp); // 别名替换
        Integer pro = curStruct.getProject();

        // 1、获取name,body
        String[] strs = splitNameBody(exp);
        String name = strs[0];
        String body = strs[1];
        System.out.println("parseStructByExp.body:" + body);

        // 2、处理结构体body内部定义的union或enum或struct，替换成【Name】，不要ID
        if (body.contains("{")) {
            String in_path = path + "/" + name;
            System.out.println("inner之前，body:" + body);
            body = parseInnerStruct(body);
            System.out.println("inner的结果，parseInnerStruct.body:" + body);
        }

        // 3、从body解析出list1和list2
        String list1 = "";//所有的成员变量类型集合
        String list2 = "";//所有的成员函数集合

        String[] list = body.split(";");
        for (String s : list) {
            s = s.trim();
            if (s.equals("")) continue;
            if (s.matches(Data.pointerReg)) { // 处理成员函数
                String f_p = path + "/" + name;
                String f_name = StrUtil.splitName(s, Data.FUNC); // 这里竟然搞成了f_p，作孽啊!
                List<SamlType> exist = samlTypeDao.findByLevelAndNameAndPathAndProject(Data.FUNC, f_name, f_p, pro);
                SamlType f_obj;
                if (exist == null || exist.isEmpty()) {
                    SamlType t = new SamlType(Data.FUNC, f_name, f_p, s, id, pro);
                    f_obj = samlTypeDao.save(t); // 这可以直接获取ID，也太厉害了吧！
                } else {
                    f_obj = exist.get(0);
                    int tid = f_obj.getId();
                    samlTypeDao.updateExp(tid, s);
                    samlTypeDao.updateFather(tid, id);
                    samlTypeDao.updateName(tid, f_name);
                }
                list2 = list2 + f_obj.getId() + ",";
                //generFuncListByExp(f_obj); //这一步不要干了，因为后面有对全部函数的处理。
            } else if (s.matches("^\\d+$")) { // 如果是数字，那就是ID，是内部定义的struct/enum/union成员
                list1 = list1 + s + ",";
            } else { // 剩下的就是普通的成员变量，类型可能是Struct或Base，查到ID即可。
                s = StrUtil.funcIgnoreWithStar(s);
                s = deleVarName(s);
                list1 = list1 + s + ",";

                // 下面是把所有变量类型替换为ID的代码，但是由于找不到，都是0，不如不替换！
                //List<SamlType> objs = samlTypeDao.findByNameAndProject(s, pro);
                //int var_id = 0;
                //for (SamlType o : objs) {
                //    int le = o.getLevel();
                //    if (le == 1 || le == 3) {
                //        var_id = o.getId();
                //        break;
                //    }
                //}
                //list1 = list1 + var_id + ",";

            }
        }
        // 4、返回[name,list1,list2]
        if (list1.equals("") && list2.equals("")) {
            return null;
        }
        String[] lists = {name, list1, list2};
        return lists;
    }

    private int[] getInnerStruct(String s) {
        int left = s.indexOf("{") + 1;// {+1的位置
        int right = 0; //}的位置，可以直接替换
        int num = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') {
                num++;
            }
            if (c == '}') {
                num--;
                if (num == 0) {
                    right = i;
                    break;
                }
            }
        }
        String tmp1 = s.substring(0, left);
        if (tmp1.contains(";")) {
            left = tmp1.lastIndexOf(";") + 1;
        } else {
            left = 0;
        }
        String tmp2 = s.substring(right);
        if (tmp2.contains(";")) {
            right = tmp2.indexOf(";") + right;
        } else {
            right = s.length();
        }

        int[] index = {left, right};
        return index;
    }

    /**
     * 解析内部结构体
     * input：整个结构体的body
     * output：把内部结构体替换为其body，再返回body
     *
     * 处理body中的内部定义：
     *      1 括号匹配，得到当前结构体
     *      2 删掉名称和{}，只要{}内的body
     *      3 in_body替换掉原来的in_exp，然后继续匹配。
     */
    @Override
    public String parseInnerStruct(String body) {
        // 1 括号匹配，得到当前结构体
        while (body.contains("{")) { // 并列多个Union，顺序处理
            int[] index = getInnerStruct(body);
            String in_exp = body.substring(index[0], index[1]);
            if (in_exp.trim().startsWith("enum")) { // 如果是enum，直接忽略，替换为空得了
                body = body.replace(in_exp, "");
            } else {
                String[] strs = splitNameBody(in_exp);
                String in_name = strs[0];
                String in_body = strs[1];
                if (in_body.contains("{")) {//如果Union嵌套，递归调用
                    System.out.println("00000000" + in_body.contains("{") + "000000");
                    in_body = parseInnerStruct(in_body);
                }
                //3 把函数声明位置替换为函数ID。通过path,in_name,pro应该是可以唯一确定的。
                body = body.replace(in_exp, in_body);
            }
        }
        return body;
    }

    /**
     * 解析内部结构体
     * input：整个结构体的body
     * output：把内部结构体存储后，替换为其ID，再返回body
     *
     * 处理body中的内部定义：
     *      1 正则拆开
     *      2 把新定义存储，父类为结构体
     *      3 把函数声明位置替换为新定义【name】，不要ID
     *      4 递归解析该新定义 【这一步还是要的，全部结构体都在这一步处理。】
     */
    @Deprecated
    private String parseInnerStructOld(String body, String in_path, int id, int pro) {
        // 1 根据正则提取内部定义
        Matcher m = Pattern.compile(Data.unionEnumStructReg).matcher(body);
        while (m.find()) {
            String in_exp = m.group().trim();
            System.out.println(in_exp + "==================in_exp");
            String[] strs = splitNameBody(in_exp);
            String in_name = strs[0];
            String in_body = strs[1];
            System.out.println("00000000" + in_name + "000000" + in_body);
            if (in_body.contains("{")) {//如果Union嵌套，递归调用
                System.out.println("00000000" + in_body.contains("{") + "000000");
                in_body = parseInnerStructOld(in_body, in_path + "/" + in_name, id, pro);
            }
            //2 把新定义内部union存储，父类为结构体
            int in_level = 0;
            if (in_exp.matches(Data.unionReg)) {
                in_level = Data.UNION;
            } else if (in_exp.matches(Data.enumReg)) {
                in_level = Data.ENUM;
            } else if (in_exp.matches(Data.structReg)) {
                in_level = Data.STRUCT;
            }
            if (in_exp.startsWith(";")) {
                in_exp = in_exp.substring(1).trim();
            }
            if (in_exp.endsWith(";")) {
                in_exp = in_exp.substring(0, in_exp.length() - 1).trim();
            }

            List<SamlType> exist = samlTypeDao.findByLevelAndNameAndPathAndProject(in_level, in_name, in_path, pro);
            SamlType in_obj;
            if (exist == null || exist.isEmpty()) {
                SamlType t = new SamlType(in_level, in_name, in_path, in_exp, id, pro);
                in_obj = samlTypeDao.save(t); // 这可以直接获取ID，也太厉害了吧！
            } else {
                in_obj = exist.get(0);
                int tid = in_obj.getId();
                samlTypeDao.updateExp(tid, in_exp);
                samlTypeDao.updateFather(tid, id);
            }

            System.out.println(in_obj.getExp() + "gggggggggggggg" + in_obj.getId());
            //3 把函数声明位置替换为函数ID。通过path,in_name,pro应该是可以唯一确定的。
            body = body.replace(in_exp, in_obj.getId() + "");
            //4 把这个新声明的exp，也要解析成List，递归。
            generStructListByExp(in_obj);
        }
        return body;
    }

    /**
     * 把exp拆分为【name，body】
     * 没有名字，默认为“no”
     * eg:
     *     union { struct llist_node fu_llist; struct rcu_head  fu_rcuhead; }
     *     [
     *     no,
     *     struct llist_node fu_llist; struct rcu_head  fu_rcuhead;
     *     ]
     */
    @Override
    public String[] splitNameBody(String exp) {
        System.out.println("ddddddd" + exp + "ddddddd");
        System.out.println("ddddddd" + exp.indexOf("{") + "ddddddd");
        System.out.println("ddddddd" + exp.lastIndexOf("}") + "ddddddd");

        String body = exp.substring(exp.indexOf("{") + 1, exp.lastIndexOf("}")).trim();
        String name = exp.substring(0, exp.indexOf("{")).replace(";", "") + " ";
        name = StrUtil.funcIgnoreWithStar(name).trim();
        name = name.replaceAll("\\(|\\)|\\*", "");
        if (name.equals("")) {
            name = "no";
        }

        String[] strs = {name, body};
        return strs;
    }

    /**
     * 解析结构体的exp。【只是简单的解析，真正exp从代码中生成绝对没这么简单啊！】
     * 输入exp，输出var_list和func_list的表达式。
     *
     * eg：{propertys:{struct module *},methods:{llseek,read,write,open}}
     *
     * @param exp
     * @return
     */
    @Deprecated
    private List<String> parseStructByExp1(String exp) {
        //exp = "{propertys:{struct module *},methods:{llseek,read,write,open}}";
        if (!exp.contains("propertys:") && !exp.contains("methods:")) {
            return null;
        }

        List<String> struct_split_exp = new ArrayList<>();

        //1、替换alias为original
        exp = alias2OriginalType(exp);
        //2、直接对exp进行Ignore处理，避免下面瞎操作了！
        exp = StrUtil.funcIgnoreNoStar(exp);
        //3、删掉表达式两头的大括号和中间无用的空格
        exp = exp.trim().replaceAll("\\{|\\}| ", "");
        System.out.println(exp);//propertys:module,methods:llseek,read,write,open

        String[] strs = exp.split("propertys:|,methods:");

        for (int i = 0; i < strs.length; i++) {
            if (strs[i].length() > 0) {
                struct_split_exp.add(strs[i]);
            }
        }
        if (struct_split_exp.size() == 1) {
            if (exp.contains("propertys:")) {
                struct_split_exp.add("");
            } else {
                struct_split_exp.add(0, "");
            }
        }

        System.out.println(struct_split_exp.size() + "————这里应该是2的，如果不是2就错了！");

        return struct_split_exp;
    }

    /**
     * 处理所有的函数定义，生成List
     */
    private void generAllFuncList(int pro) {
        //一条条数据处理，每次都要读库太麻烦了，有空考虑Redis吧
        List<SamlType> funcs = samlTypeDao.findByLevelAndStateAndProject(Data.FUNC, Data.State, pro);
        if (funcs == null || funcs.size() == 0) {
            return;
        }
        for (int i = 0; i < funcs.size(); i++) {
            SamlType curFunc = funcs.get(i);
            generFuncListByExp(curFunc);
        }
    }

    /**
     * 处理所有的函数声明，生成List
     */
    private void generAllFuncDeclareList(int pro) {
        //一条条数据处理，每次都要读库太麻烦了，有空考虑Redis吧
        List<SamlType> funcs = samlTypeDao.findByLevelAndStateAndProject(Data.FUNC_DECLARE, Data.State, pro);
        if (funcs == null || funcs.size() == 0) {
            return;
        }
        for (int i = 0; i < funcs.size(); i++) {
            SamlType curFunc = funcs.get(i);
            generFuncListByExp(curFunc);
        }
    }

    /**
     * 根据function(level=2)的exp，生成List1和List2，再存入库中。
     *
     * 1、输入：
     * exp
     *      是type表中的exp字段，格式：
     *      ssize_t (*read) (struct file *, char __user *, size_t, loff_t *)
     *      long do_sys_open(int dfd, const char __user *filename, int flags, umode_t mode);
     *
     * structName
     *      是当前函数所在的结构体名。
     *      file_operations
     *
     * 2、输出：
     *      type_name：函数名，存入type表的name字段。
     *          file_operations.read
     *      in_list：函数的输入类型。
     *          解析出所有类型
     *          存入list表
     *          把刚存入的list的id存入exp的list1字段。
     *
     *      out_list：返回类型。
     *          解析出返回值类型
     *
     *          如果是基本类型:
     *              从type表查到对应的type_id。若没有，再去typedef中查。若还没有，报错“没有XX类型”(#插入到type中#)。
     *              查到对应的type_id，存入list表的members字段，也要填上其他字段：
     *                  member_classify：member的类型，0表示type类型的list(复合类型)，1表示expression类型的list，2表示relation中的list。
     *                  type_compound：一个methods，拉取输入参数时，存储compound=8，拉取输出时，存储compound=8。
     *                      ???总感觉这个不对劲。到底算是聚合还是method?【应该看是type的list还是exp的list】？？
     *
     *
     *          如果是复合类型：
     *              解析出对应的基本类型
     *              每一个基本类型都去type中查，
     *              去list中查。若没有，插入到list中。
     *
     *          list2字段。
     *
     *          ssize_t
     *
     *
     * 读出所有function的exp，根据exp生成List1和List2，再存入库中。
     */
    @Override
    public void generFuncListByExp(SamlType curFunc) {
        int id = curFunc.getId();
        //step1:解析函数表达式
        String[] func_split_exp = parseFuncByExp(curFunc.getExp());
        System.out.println("========paramIn:===" + func_split_exp[1] + "==========");

        //step2:更新saml_type函数名name
        String func_name = func_split_exp[0];
        //if (curFunc.getName().equals("")) { // 更新个别没名字的
        samlTypeDao.updateName(curFunc.getId(), func_name);
        //}

        //step3:更新两个list
        //更新list1即param_in，更新list2即param_out。两个list需要判断list表中是否存在，得到list_id，然后插入到type表。
        int listCompound = 8;
        String param_in = func_split_exp[1];// 去重
        if (!param_in.equals("")) {
            param_in = StrUtil.distinct(param_in);
        }
        String param_out = func_split_exp[2];
        if (!param_out.equals("")) {
            param_out = StrUtil.distinct(param_out);
        }
        if (param_in.contains("(")) {// 参数表中有函数时，如何解析
            String new_path = curFunc.getPath() + "/" + func_name;
            param_in = parsePointerFunc(Data.paramFuncReg, Data.PARA_FUNC_DECLARE, param_in, new_path, id);
        }

        //处理param_in，进行带星号忽略，去掉变量名，删空格
        param_in = StrUtil.funcIgnoreWithStar(param_in);
        System.out.println("删除变量名之前的param_in===" + param_in);
        //对于含有变量名的参数，删除“类型 变量名”中的“ 变量名”
        param_in = deleVarName(param_in);
        //去掉param_in的所有空格，包括词间的空格
        param_in = param_in.replaceAll(" ", "");

        //去重
        if (!param_in.equals("")) {
            param_in = StrUtil.distinct(param_in);
        }

        // 参数表中无函数时，如何解析【上面函数参数已经替换成ID了】
        int list1_id = samlListService.insertUpdateListId(param_in, listCompound);
        int list2_id = samlTypeService.typeExist(param_out);//【list2直接存储type_id得了】
        System.out.println(list1_id + "++++++" + list2_id);
        samlTypeDao.updateListExp(curFunc.getId(), param_in, param_out); //费半天劲解析的两个表达式，不存实在是太可惜啦
        samlTypeDao.updateList(curFunc.getId(), list1_id, list2_id);
    }

    /**
     * 处理param_in中的函数参数：
     *      1 正则拆开
     *      2 把函数声明存储，父类为函数
     *      3 把函数声明位置替换为函数ID
     *      4 解析函数声明
     *
     */
    private String parsePointerFunc(String reg, int level, String body, String new_path, int id) {
        // 1 根据正则提取函数指针参数
        Matcher m = Pattern.compile(reg).matcher(body);
        while (m.find()) {
            String f_exp = m.group().trim();
            System.out.println("匹配到的指针" + f_exp);
            //System.out.println(m.start() + "====" + m.end());

            //2 存储参数的函数声明： level=15|path=xxx/函数名|members_str|father=函数ID
            String f_name = StrUtil.splitName(f_exp, Data.FUNC);

            List<SamlType> exist = samlTypeDao.findByLevelAndNameAndPathAndProject(level, f_name, new_path, pro);
            SamlType f_obj;
            if (exist == null || exist.isEmpty()) {
                SamlType t = new SamlType(level, f_name, new_path, f_exp, id, pro);
                f_obj = samlTypeDao.save(t); // 这可以直接获取ID，也太厉害了吧！
            } else {
                f_obj = exist.get(0);
                int tid = f_obj.getId();
                samlTypeDao.updateExp(tid, f_exp);
                samlTypeDao.updateFather(tid, id);
            }

            //3 把函数声明位置替换为函数ID。通过Declare和f_exp，应该是可以唯一确定的。
            //SamlType f_obj = samlTypeDao.findByPathAndExpAndProject(new_path, f_exp, pro).get(0);
            body = body.replace(f_exp, f_obj.getId() + "");
            //4 把这个函数参数声明的exp，也要解析一下吧。递归调用当前函数就好。
            if (level == Data.PARA_FUNC_DECLARE) {
                generFuncListByExp(f_obj);
            }
        }
        return body;
    }

    /**
     * 工具类，输入函数声明exp，输出一个[func_name,param_in,param_out]的三元素String[]
     *
     * map1、funcIgnoreNoStar(s)，删掉每一项中的需要忽略的东西。
     *
     * map2、deleteVarName(s)，对于含有变量名的函数，删除“类型 变量名”中的“ 变量名”。
     *
     * 只有声明的函数：函数名（指针）是在括号中的，split长为3。
     *      ssize_t (*read) (struct file *, char __user *, size_t, loff_t *);
     *      【不知道这种情况会不会有变量名？？此处按照有变量名的情况处理。也就是调用deleVarName()】
     *
     * 含有变量名的函数：函数名不在括号中，split长为2，第一项为param_out和func_name。
     *      long do_sys_open(int dfd, const char __user *filename, int flags, umode_t mode);
     *
     * 其实现在按括号split有问题，如果参数中出现了括号，就会乱套了！！
     *      是函数指针时，0是“param_out”，1是“func_name”，剩下全是param_in。
     *          找到第1个“(”前面是0，第1个“(”和第1个“)”之间是1，第2个“(”后面是param_in。
     *      不是函数指针时，0是“param_out func_name”，剩下全是param_in。
     *          找到第1个“(”前面是0，后面是param_in。
     *
     *
     * @param exp
     * @return
     */
    @Override
    public String[] parseFuncByExp(String exp) {
        //1、判断func_type，解析是依赖这个的，不判断就没法解析。由于判断依据“*标识符”，而Ignore包括“*”，所以放Ignore前面。
        int func_type = getFuncType(exp);
        //2、替换alias为original，以避免不必要的麻烦，比如typeExist就可以直接查saml_type而不需要alias了。
        exp = alias2OriginalType(exp);
        //3、直接对exp进行Ignore处理，避免下面瞎操作了！
        exp = StrUtil.funcIgnoreNoStar(exp); // 这里不应该删掉我的星的！因为参数函数需要星作为标识解析的！
        System.out.println("---" + exp + "---");

        //4、字符串拆分，成为三部分
        String param_out = "";
        String func_name = "";
        String param_in = "";
        if (func_type == POINTER_FUNC) {
            String tmp = exp.substring(exp.indexOf(")") + 1);//这是带括号的参数部分，包括空格
            param_in = tmp.substring(tmp.indexOf("(") + 1, tmp.lastIndexOf(")"));

            //删掉参数前的星号
            exp = exp.replaceAll("\\*", "").trim();
            param_out = exp.substring(0, exp.indexOf("("));
            //这里的func_name是有问题的，对于函数名含括号的情况就不行：(*(int)a)
            //只能用【括号匹配】来解决【？？】
            func_name = exp.substring(exp.indexOf("(") + 1, exp.indexOf(")"));

        } else if (func_type == GLOBAL_FUNC) {
            param_in = exp.substring(exp.indexOf("(") + 1, exp.lastIndexOf(")"));
            // 可以删掉参数前的星号，还要考虑多个空格时用“\\s+”多个空白符区分
            exp = exp.replaceAll("\\*", "");
            String[] tmp = exp.substring(0, exp.indexOf("(")).trim().split("\\s+");
            param_out = tmp[0];
            func_name = tmp[1];
        }
        // 到此为止，所有的参数还包含空格和变量名，但是已经去掉了ignoreword。

        // ——————接下来，先处理带函数指针的参数，才能删变量名、去空格等————，所以，干脆把此时的param_in传过去得了。

        //对于含有变量名的参数，删除“类型 变量名”中的“ 变量名”
        //param_in = deleVarName(param_in);

        //5、去掉param_in的所有空格，包括词间的空格
        //param_in = param_in.replaceAll(" ", "");
        param_in = param_in.trim();
        param_out = param_out.replaceAll(" ", "");
        func_name = func_name.replaceAll(" ", "");

        System.out.println("===" + func_name + "====");
        System.out.println("===" + param_in + "====");
        System.out.println("===" + param_out + "====");

        String[] list = {func_name, param_in, param_out};

        return list;
    }

    /**
     * 把表达式中的alias全部替换为相应的originalType，以减少不必要的麻烦。
     *
     * 单词边界匹配：\b，需要转义 \\b。必须匹配单词替换啊，否则就乱套了！
     *
     * 注意，一般的字符串匹配，默认都是最长匹配。
     *      虽然这里没有用到，但是可能会出现最短/最长匹配的情况。
     *      如：unsigned int换成int，而非unsigned换成int变成int int。不过这个暂时不用alias，而是在下面Ignore中处理。
     *
     * */
    @Override
    public String alias2OriginalType(String exp) {
        List<Alias> allAlias = aliasDao.findAll();
        for (int i = 0; i < allAlias.size(); i++) {
            String alias = allAlias.get(i).getAlias();
            if (exp.contains(alias)) {
                System.out.println("alias:" + alias);
                exp = exp.replaceAll("\\b" + alias + "\\b", allAlias.get(i).getOriginalType());
            }
        }
        return exp;
    }

    /**
     * 区分：1 函数指针 type (*name)(xxx)   2 普通函数 type name(xxx)  3 参数表有函数的情况
     *
     * 如何判断func_type啊？解析是依赖这个的，不判断就没法解析。
     *      只有声明的函数：ssize_t (*read) (struct file *, char __user *, size_t, loff_t *);
     *      含有变量名的函数：long do_sys_open(int dfd, const char __user *filename, int flags, umode_t mode);
     *
     * 利用regex，看第一个"()"中，是否是“*标识符”格式。
     *      标识符：[a-zA-Z\_][0-9a-zA-Z\_]*
     *      *需要转义：\\*
     *
     *      匹配“(*标识符)”，且尽可能短。
     *
     * @return
     */
    private static int getFuncType(String s) {
        System.out.println("getfuncType当前解析的表达式为：" + s);
        String func_name = s.substring(s.indexOf("(") + 1, s.indexOf(")")).replaceAll(" ", "");
        String regex = "(^\\*[a-zA-Z\\_][0-9a-zA-Z\\_]*?)"; // *?表示非贪婪。

        //Matcher matcher = Pattern.compile(regex).matcher(func_name);
        //if (matcher.matches()) {
        if (func_name.matches(regex)) {
            //boolean flag1 = s.matches(paramFuncReg);
            //boolean flag2 = s.matches(pointerReg);
            //if (flag1 && flag2) {
            //    return POINTER_PARAFUNC_FUNC;
            //} else if (flag1 && !flag2) {
            //    return GLOBAL_PARAFUNC_FUNC;
            //} else if (!flag1 && flag2) {
            return POINTER_FUNC;
        } else {
            return GLOBAL_FUNC;
        }
    }

    /**
     * 对于只有声明的函数，无需操作。
     * 对于含有变量名的函数，删除“类型 变量名”中的“ 变量名”。
     *
     * 只有声明的函数：ssize_t (*read) (struct file *, char __user *, size_t, loff_t *);
     * 含有变量名的函数：long do_sys_open(int dfd, const char __user *filename, int flags, umode_t mode);
     *
     *
     * contains：
     * 包含空格时，说明是有变量名的函数，要把“空格+变量名”删掉，
     * 不含空格时，说明就是类型，直接返回即可。
     *
     * @param s
     * @return
     */
    private static String deleVarName(String s) {
        System.out.println("deletName:" + s);
        String[] strs = s.split(",");
        String result = "";
        boolean flag = false;
        for (int i = 0; i < strs.length; i++) {
            String tmp = strs[i].trim();
            if (tmp.contains(" ")) {
                flag = true;
                result = result + tmp.split(" ")[0] + ",";
            }
        }
        if (flag) {
            return result.substring(0, result.length() - 1);
        } else {
            return s;
        }
    }

}
