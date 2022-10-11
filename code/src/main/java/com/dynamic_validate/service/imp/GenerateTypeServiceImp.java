package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.IncludeImportDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.IncludeImport;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.GenerateTypeService;
import com.dynamic_validate.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GenerateTypeServiceImp implements GenerateTypeService {
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private IncludeImportDao includeImportDao;

    /**
     * 产生字段：level,path,name,exp,pro
     */
    @Override
    public void generLevel4_6ByPath(int pro) {
        generFileByPath(pro); // 产生文件

        generSubsysDentry(pro);// 先存储所有的dentry和subsys
        generLevel5_6Exp(pro);// 再更新dentry和subsys的exp

        updateFatherByPath(pro); //更新文件、结构体、函数的father
    }

    /**
     * 产生字段：level,path,name,exp,pro
     * @param path 这个是带着.h或.c的文件相对路径！
     */
    @Override
    public void generLevel4_6ByPath(String path, int pro) {
        generOneFileByPath(path, pro); // 产生一个当前文件

        generOneSubsysDentry(path, pro);// 先存储所有的dentry和subsys
        generLevel5_6Exp(pro);// 再更新dentry和subsys的exp【全部更新好了，一共没几个】
        //updateFatherByPath(pro); //更新文件、结构体、函数的father【全部更新好了，太慢了，还是别更新了，最后更新一下得了】
    }

    /**
     * 处理typedef：
     *      0、修改util的解析，typedef不应该去掉花括号后面的名称，那是别名。
     *      1、如果有花括号
     *          存储为各自的类型
     *          存储为alias
     *      2、
     */
    @Override
    public void dealTypedef(int pro) {

    }

    /**
     * 更新dentry和subsys的exp
     */
    private void generLevel5_6Exp(int pro) {
        List<SamlType> list_56 = samlTypeDao.findByLevelAndProject(Data.DENTRY, Data.SUBSYS, pro);
        for (SamlType t : list_56) {
            generOneLevel5_6Exp(t, pro);
        }
    }

    private void generOneLevel5_6Exp(SamlType t, int pro) {
        String path = t.getPath();
        if (path.equals("/")) {
            path = t.getName();
        } else {
            path = path + "/" + t.getName();
        }

        String list1 = samlTypeDao.getListByLevel(path, Data.FILE, pro).get("exp");
        String list2 = samlTypeDao.getListByLevel(path, Data.DENTRY, pro).get("exp");
        if (list1 == null) {
            list1 = "";
        }
        if (list2 == null) {
            list2 = "";
        }
        String exp = "" + list1 + ";" + list2;
        samlTypeDao.updateExp(t.getId(), exp);
    }

    /**
     * [level,path,name,exp,pro] 没有father
     *
     * 【只能第一次刚生成文件后执行】
     * 根据所有结构体和函数的path，生成File。
     * 按照path分组。
     */
    @Override
    public void generFileByPath(int pro) {
        List<String> allFilePath = samlTypeDao.getAllFilePath(pro);
        for (int i = 0; i < allFilePath.size(); i++) {
            String path = allFilePath.get(i);
            //System.out.println(path);
            generOneFileByPath(path, pro);
        }
        //include/asm-generic/module.h
    }

    /**
     * @param path 这个是文件的path，带着.h或.c的那种！
     */
    @Override
    public void generOneFileByPath(String path, int pro) {
        System.out.println("====???????????????====" + path);
        if (!path.endsWith(".h") && !path.endsWith(".c")) { //不是文件，大概率是结构体的内部函数、函数的函数参数等原因
            //System.out.println("=============当前path不是文件：" + path);
            return;
        }
        String file_name = path.substring(path.lastIndexOf("/") + 1);
        String file_path = path.replace("/" + file_name, "");
        Map<String, String> list1 = samlTypeDao.getListByLevel(path, Data.FUNC, pro);
        //System.out.println("-----------" + list1.get("exp"));
        String list1_exp = list1.get("exp") == null ? "" : list1.get("exp");
        Map<String, String> list2 = samlTypeDao.getListByLevel(path, Data.STRUCT, pro);
        //System.out.println("-----------" + list2.get("exp"));
        String list2_exp = list2.get("exp") == null ? "" : list2.get("exp");
        String exp = list1_exp + ";" + list2_exp;
        List<SamlType> exist = samlTypeDao.findByLevelAndNameAndPathAndProject(Data.FILE, file_name, file_path, pro);
        if (exist == null || exist.isEmpty()) {
            System.out.println("不存在");
            SamlType file = new SamlType(Data.FILE, file_name, file_path, exp, 0, pro);
            samlTypeDao.save(file);
        } else if (exist.size() == 1) {
            System.out.println("存在" + exp);
            int id = exist.get(0).getId();
            samlTypeDao.updateExp(id, exp);
        } else {
            System.out.println("有重复，看来level,name,path,pro不能作为唯一id？");
            System.out.println(exist);
        }
    }

    /**
     * [level,path,name,exp,father,pro] 有father，子系统的father就是0
     *
     * 【只能第一次刚生成文件后执行】
     * 根据path生成子系统和目录
     * 1、先存储: level,path,name,father,pro
     * 2、后更新: exp【因为dentry在subsys之后生成
     *
     */
    private void generSubsysDentry(int pro) {
        List<String> allDentryPath = samlTypeDao.getAllDentryPath(pro);
        for (int i = 0; i < allDentryPath.size(); i++) {
            String dentry_path = allDentryPath.get(i); // 最后没有/
            generOneSubsysDentry(dentry_path, pro);
        }
    }

    /**
     * @param dentry_path 这个是纯路径，不能有.h或.c！如果有，需要处理
     */
    private void generOneSubsysDentry(String dentry_path, int pro) {
        if (dentry_path.endsWith(".h") || dentry_path.endsWith(".c")) { //传成文件时，去掉即可
            dentry_path = dentry_path.substring(0, dentry_path.lastIndexOf("/"));
        }
        String subsysName = dentry_path;
        String dentryName = "";
        if (dentry_path.contains("/")) { // 有斜杠时才赋值 include/linux
            subsysName = dentry_path.substring(0, dentry_path.indexOf("/")); // include
            dentryName = dentry_path.replace(subsysName + "/", ""); // linux
        }

        // 根目录下暂时没有文件，因此不用考虑dentry_path为“/”情况。

        // 存储subsys
        List<SamlType> exist = samlTypeDao.findByLevelAndNameAndPathAndProject(Data.SUBSYS, subsysName, "/", pro);
        if (exist == null || exist.isEmpty()) {
            SamlType subsys = new SamlType(Data.SUBSYS, subsysName, "/", "", 0, pro);
            samlTypeDao.save(subsys);
        } else {
            int id = exist.get(0).getId();
            samlTypeDao.updateFather(id, 0);
        }

        if (!dentryName.equals("")) { // 如果有目录，存储dentry
            System.out.println("有目录");

            List<SamlType> exi = samlTypeDao.findByLevelAndNameAndPathAndProject(Data.DENTRY, dentryName, subsysName, pro);
            int sub_id = samlTypeDao.findByNameAndProject(subsysName, pro).get(0).getId();
            if (exi == null || exi.size() == 0) {
                SamlType dentry = new SamlType(Data.DENTRY, dentryName, subsysName, "", sub_id, pro);
                samlTypeDao.save(dentry);
            } else {
                int id = exist.get(0).getId();
                samlTypeDao.updateFather(id, sub_id);
            }

        }
    }


    @Value("${finish.type.fatherRelation.fromFirstNotdeal}")
    private boolean fromFirstNotdeal;

    /**
     * 生成father字段，文件4、结构体3、函数2。
     * 根据path就可以啦。
     *
     */
    @Override
    public void updateFatherByPath(int pro) {
        List<SamlType> func_list;
        if (fromFirstNotdeal) {
            func_list = samlTypeDao.findByLevelAndProjectNotdeal(Data.FUNC, Data.FILE, pro);
        } else {
            func_list = samlTypeDao.findByLevelAndProject(pro);
        }
        for (SamlType t : func_list) {
            String p = t.getPath();
            String fatherPath = "";
            String fatherName = "";
            if (!p.contains("/")) {
                fatherName = p;
                fatherPath = "/";
            } else {
                if (t.getLevel() == Data.FILE) {
                    fatherPath = p.substring(0, p.indexOf("/")); // 文件的father是dentry时，path就是子系统，
                    fatherName = p.substring(p.indexOf("/") + 1);// 剩下的是dentry的name
                } else {
                    fatherPath = p.substring(0, p.lastIndexOf("/")); // 结构体、函数的father是File或结构体时
                    fatherName = p.substring(p.lastIndexOf("/") + 1);
                }
            }
            System.out.println(t.getId() + "aaaaaaaaaaaaaaaaaaa" + fatherPath + "----" + fatherName);
            SamlType father = samlTypeDao.findByNameAndPathAndProject(fatherName, fatherPath, pro).get(0);
            samlTypeDao.updateFather(t.getId(), father.getId());
        }
    }

    /**
     * 从exp中生成name
     */
    @Override
    public void generNameByExp(int pro) {
        List<SamlType> func_list = samlTypeDao.findByLevelAndProject(Data.FUNC, Data.STRUCT, pro);
        for (SamlType t : func_list) {
            String p = t.getPath();
            if (p.endsWith(".h") || p.endsWith(".c")) {
                generOneNameByExp(t);
            }
        }
    }

    /**
     * 从exp中生成指针函数的name，带*和括号的。
     */
    @Override
    public void generfuncPointerNameByExp(int pro) {
        List<SamlType> func_list = samlTypeDao.findByLevelAndProject(Data.FUNC, pro);
        for (SamlType t : func_list) {
            String exp = t.getExp();
            if (exp.matches(Data.pointerReg)) {
                String name = StrUtil.splitName(exp, t.getLevel());
                System.out.println(name);
                samlTypeDao.updateName(t.getId(), name);
            }
        }
    }

    @Override
    public void generOneNameByExp(SamlType t) {
        String exp = t.getExp();
        String name = StrUtil.splitName(exp, t.getLevel());
        System.out.println(name);
        samlTypeDao.updateName(t.getId(), name);
    }

    @Override
    public void generNameByExp(String path, int pro) {
        List<SamlType> func_list = samlTypeDao.findByLevelAndPathAndProject(Data.FUNC, Data.STRUCT, path, pro);
        for (SamlType t : func_list) {
            generOneNameByExp(t);
        }
    }

    @Override
    public void typeDistinctByExp() {
        List<String> list = samlTypeDao.findDistinctByExpPath();
        for (String idstr : list) {
            String[] ids = idstr.split(",");
            for (int i = 0; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                SamlType oi = samlTypeDao.findSamlTypeById(id);
                if (oi.getName() == null || oi.getName().equals("no") || oi.getName().equals("")) {
                    samlTypeDao.delete(oi);
                    System.out.println(oi.getId());
                }
            }
        }
    }

    /**
     * 去重，按照level,path,name唯一识别，否则就是重复。如果name有问题就查exp。
     */
    @Override
    public void typeDistinct() {
        String reg = ".*\\)\\s*\\(.*";
        List<String> list = samlTypeDao.findDistinctIds();
        //List<String> list = samlTypeDao.findDistinctByExpPath();
        for (String idstr : list) {
            String[] ids = idstr.split(",");
            int id1 = Integer.parseInt(ids[0]);
            SamlType o1 = samlTypeDao.findSamlTypeById(id1);
            String o1_exp = o1.getExp();
            for (int i = 1; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                SamlType oi = samlTypeDao.findSamlTypeById(id);
                String oi_exp = oi.getExp();
                if (oi_exp.matches(reg)) {
                    oi_exp = oi_exp.replaceAll(reg, ")(");
                }
                if (oi_exp.equals(o1_exp)) {
                    samlTypeDao.delete(oi);
                    //System.out.println("删掉重复：" + oi.getExp());
                } else {
                    //System.out.println("exp竟然不一样，对比下看看：");
                    //System.out.println(o1.getProject() + ":" + o1.getExp());
                    //System.out.println(oi.getProject() + ":" + oi.getExp());
                    if (o1.getProject() == 7) {
                        System.out.println("o1是7");
                    } else {
                        System.out.println("o1是6");
                    }
                }
            }
        }
    }

    @Override
    public void includeDistinct() {
        //String reg = ".*\\)\\s*\\(.*";
        List<String> list1 = includeImportDao.findDistinctIds();
        distIncludeList(list1);
        List<String> list2 = includeImportDao.findDistinctLevel914Ids();
        distIncludeList(list2);

    }

    private void distIncludeList(List<String> list) {
        for (String idstr : list) {
            String[] ids = idstr.split(",");
            int id1 = Integer.parseInt(ids[0]);
            IncludeImport o1 = includeImportDao.findById(id1);
            String o1_exp = o1.getExp();
            for (int i = 1; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                IncludeImport oi = includeImportDao.findById(id);
                String oi_exp = oi.getExp();
                if (oi_exp.equals(o1_exp) || o1_exp.equals(oi_exp + ";") || oi_exp.equals(o1_exp + ";")) {
                    includeImportDao.delete(oi);
                    //System.out.println("删掉重复：" + oi.getExp());
                } else {
                    System.out.println("exp竟然不一样，对比下看看：");
                    System.out.println(o1.getPro() + ":" + o1.getExp() + ",name:" + o1.getName());
                    System.out.println(oi.getPro() + ":" + oi.getExp() + ",name:" + oi.getName());
                    if (o1.getPro() == 7) {
                        System.out.println("o1是7");
                    } else {
                        System.out.println("o1是6");
                    }
                }
            }
        }
    }


    @Override
    public void generIncludeName() {
        List<IncludeImport> struct_list = includeImportDao.findByLevel(Data.STRUCT_DECLARE, Data.FUNC_DECLARE);
        for (IncludeImport t : struct_list) {
            String exp = t.getExp();
            String name = "";
            if (t.getName().equals("no") && t.getLevel() == Data.STRUCT_DECLARE) {
                name = exp.split(" ")[1];
            } else {
                name = StrUtil.splitName(exp, t.getLevel());
            }
            //System.out.println(name);
            includeImportDao.updateName(t.getId(), name);
        }
    }
}
