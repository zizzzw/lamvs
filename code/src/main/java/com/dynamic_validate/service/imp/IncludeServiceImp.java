package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.IncludeImportDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.IncludeImport;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.DataImportService;
import com.dynamic_validate.service.ReportService;
import com.dynamic_validate.service.GenerateTypeService;
import com.dynamic_validate.service.IncludeService;
import com.dynamic_validate.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
public class IncludeServiceImp implements IncludeService {
    @Autowired
    private IncludeImportDao includeImportDao;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private DataImportService dataImportService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private GenerateTypeService generateTypeService;

    /**
     * 关于函数声明和include：
     *      1、Func_Declare, Struct_Declare, include_File={Func_Declare,Struct_Declare}
     *          include_file_list:
     *              select * from where fahter = curFile and level=Include and originType!=null and originType !=0
     *
     *      2、func_declare_list：
     *          select * from where father=curFile and level=Func_Declare
     *
     *          select * from where father in include_file_list and level=Func_Declare
     *
     *      3、func_declare_list：
     *          select * from where father=curFile and level=Struct_Declare
     *
     *
     * 1、先把带问号的相对路径补充完整
     *      path去掉“/文件名”替换掉“?”即可。
     * 2、生成name并找到origin，更新到include_import表中。
     *
     */
    @Override
    public void finishQuestExp(int pro) {
        List<IncludeImport> q_include = includeImportDao.getQuestInclude();
        for (IncludeImport t : q_include) {
            String p = t.getPath();
            p = p.substring(0, p.lastIndexOf("/"));
            String new_exp = t.getExp().replace("?", p);
            includeImportDao.updateExp(t.getId(), new_exp);
        }
    }

    /**
     * 1、生成所有文件的直接include_file。
     * 2、再对所有的.h文件，生成间接include_file。
     *
     *
     * 3、include_result生成递归出口的include的两个declare列表，include_file为“null”。type早已生成所有文件的两个define列表。
     * 4、include_result生产所有文件的两个declare列表和include_file。
     */
    @Override
    public void generResult(int pro) {

    }

    /**
     * 1、生成include的文件ID，先生成有的，
     * 2、对于没有的，执行copyIncludeFile，如果源码中还是没有，就报错存到报告中。
     * 对于新生成的include，不作处理，再说【因为已经超出范围了】。
     *
     */
    @Override
    public void generIncludeOrigin(int pro, String rep_id) {
        List<String> allInclude = includeImportDao.getInclude(pro);
        for (String p : allInclude) {
            int origin_level = Data.FILE;
            String name = p.substring(p.lastIndexOf("/") + 1);
            String path = p.replace("/" + name, "");
            List<SamlType> li = samlTypeDao.findByLevelAndNameAndPathAndProject(origin_level, name, path, pro);

            if (li == null || li.isEmpty()) {
                System.out.println("include文件不存在啊！");
                includeImportDao.updateIncludeOrigin(p, 0);
            } else {
                int origin_id = li.get(0).getId();
                includeImportDao.updateIncludeOrigin(p, origin_id);
            }
        }

    }

    public void generIncludeOrigin1(int pro, String rep_id) {
        List<String> allInclude = includeImportDao.getInclude(pro);
        for (String p : allInclude) {
            int origin_level = Data.FILE;
            String name = p.substring(p.lastIndexOf("/") + 1);
            String path = p.replace("/" + name, "");
            List<SamlType> li = samlTypeDao.findByLevelAndNameAndPathAndProject(origin_level, name, path, pro);

            if (li == null || li.isEmpty()) {
                System.out.println("include文件不存在啊！");
                System.out.println("开始复制并解析该include文件！");
                boolean includeExist = copyOneInclude(p, pro, rep_id);
                if (!includeExist) {
                    continue;
                } else {
                    li = samlTypeDao.findByLevelAndNameAndPathAndProject(origin_level, name, path, pro);
                }
            }
            int origin_id = li.get(0).getId();
            includeImportDao.updateIncludeOrigin(p, origin_id);
        }

    }

    @Override
    public void copyAllIncludeFile(int pro, String rep_id) {
        List<String> includes = includeImportDao.getLackInclude(pro);
        for (String s : includes) {
            copyOneInclude(s, pro, rep_id);
        }
    }

    private boolean copyOneInclude(String s, int pro, String rep_id) {
        String p = s.substring(0, s.lastIndexOf("/"));
        String fn = s.substring(s.lastIndexOf("/") + 1);
        String dcs = "";
        if (pro == Data.Pro6) {
            dcs = Data.copy_sour_linux;
        } else if (pro == Data.Pro7) {
            dcs = Data.copy_sour_guest;
        } else if (pro == Data.Pro8) {
            dcs = Data.copy_sour_host;
        }
        String source = dcs.replace("\\", "/") + "/" + s;
        String dest = Data.source_guest.replace("\\", "/") + "/" + s;
        File sf = new File(source);
        if (!sf.exists()) { //如果源文件不存在，直接存储错误，返回false。
            //IncludeImport inc = includeImportDao.getByExpAndPro(s, pro).get(0);
            IncludeImport inc = new IncludeImport(source);
            reportService.saveError(Data.IncludeLackError, inc, rep_id);
            return false;
        } else {
            File df = new File(dest);
            String dp = dest.replace("/" + fn, "");
            System.out.println(dp + "??????????");
            File dpf = new File(dp);
            if (!dpf.exists()) { //如果目录不存在，先递归创建目录再说。
                dpf.mkdirs();
            }
            try {
                Files.copy(sf.toPath(), df.toPath());
                // 把这个新文件重新跑一遍design
                dataImportService.importFromOneFile(dest, Data.source_guest, pro);
                generateTypeService.generNameByExp(p, pro);
                generateTypeService.generLevel4_6ByPath(s, pro);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("出错啦，大概率是复制重复了吧");
                return false;
            }


        }
    }

    @Override
    public void generNameAndOrigin(int level, int pro) {
        List<IncludeImport> func_list = includeImportDao.findByLevelAndPro(level, pro);
        for (IncludeImport t : func_list) {
            int origin_id = 0;
            String p = t.getExp();
            int origin_level = 0;
            if (level == Data.FUNC_DECLARE) { // 文件中的函数声明，包括extern
                origin_level = Data.FUNC;
            } else if (level == Data.STRUCT_DECLARE) {
                origin_level = Data.STRUCT;
            }

            // 1 生成name
            p = StrUtil.splitName(p, level);
            includeImportDao.updateName(t.getId(), p); // 更新名称

            // 2 找到origin
            List<SamlType> same_name_func = samlTypeDao.findByNameAndLevelAndProject(p, origin_level, pro);
            if (same_name_func == null || same_name_func.size() == 0) {
                System.out.println("=====没找到函数/结构体定义【这种情况不少吧】：====" + p);
            } else if (same_name_func.size() > 1) {
                System.out.println("===出现重名函数/结构体===寻找第一个father是文件的函数===" + p);
                // 太草率了，按理说该找include的文件
                for (SamlType f : same_name_func) {
                    // 这里应该不会出现father不存在的情况，按理说所有的函数都生成了其文件
                    if (samlTypeDao.findSamlTypeById(f.getFather()).getLevel() == Data.FILE) {
                        origin_id = f.getId();
                        break;
                    }
                }
            } else { // 只有一个元素
                origin_id = same_name_func.get(0).getId();
            }
            includeImportDao.updateOriginType(t.getId(), origin_id); // 更新origin_id
        }

    }


}
