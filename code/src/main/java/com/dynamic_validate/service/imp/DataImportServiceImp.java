package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.AliasDao;
import com.dynamic_validate.dao.DemandInvokeDao;
import com.dynamic_validate.dao.SamlListDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.DemandInvoke;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.DataImportService;
import com.dynamic_validate.service.DemandInvokeService;
import com.dynamic_validate.service.SamlTypeService;
import com.dynamic_validate.util.FileUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataImportServiceImp implements DataImportService {
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private SamlListDao samlListDao;
    @Autowired
    private AliasDao aliasDao;
    @Autowired
    private DemandInvokeDao demandInvokeDao;
    @Autowired
    private DemandInvokeService demandInvokeService;
    @Autowired
    private SamlTypeService samlTypeService;

    @Override
    public void generateAlias() {

    }

    /**
     * 【未完，暂时不用，先搁置】
     * 从树形调用文本的Excel(需求规范模板.xlsx)中生成调用关系(saml_demand.xlsx)：
     *
     * 开始：2021-11-24 20:31:54
     * 结束：
     */
    @Override
    public void demandTree2SamlDemand() {
        // 设定Excel文件所在路径
        String fileName = "G:/研究生/毕设/我的毕设/系统实现/checkSys数据/5-完整数据1/需求规范模板_2021-8-9.xlsx";
        try {
            // 获取一个工作簿
            FileInputStream fileInputStream = new FileInputStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int firstRow = 0;
            // 获取sheet的最后一行
            int lastRow = sheet.getLastRowNum();

            for (int i = firstRow; i < lastRow; i++) {
                XSSFRow row = sheet.getRow(i);

                int lastCol = row.getLastCellNum();
                XSSFCell cell = row.getCell(lastCol - 1);
                cell.setCellType(Cell.CELL_TYPE_STRING);// 强制作为String处理
                String invokable_relation = cell.getStringCellValue().trim();
                //写不下去了，好困，先弄下实习报告好了。2021-11-24 21:16


                System.out.println(invokable_relation + "  ");
                String[] funcsName = invokable_relation.split(";|；");

                String fun1Name = funcsName[0];
                String fun2Name = funcsName[1];
                SamlType func1 = samlTypeDao.findByNameAndLevel(fun1Name, Data.FUNC).get(0);
                int func1Id = 0, func2Id = 0;
                if (func1 != null) {
                    func1Id = func1.getId();
                }
                SamlType func2 = samlTypeDao.findByNameAndLevel(fun2Name, Data.FUNC).get(0);
                if (func2 != null) {
                    func2Id = func2.getId();
                }
                row.getCell(lastCol - 1).setCellValue(func1Id);
                row.getCell(lastCol - 2).setCellValue(func2Id);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            workbook.write(fileOutputStream);

            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 仅针对：从表中导出xlsx仍需填写func1和func2的情况。
     *
     * 完善调用关系的demand_path.xlsx文件：
     * 利用POI解析Excel，提取出两个函数，从数据库找到对应的type_id，填入func1和func2字段。
     */
    @Override
    public void finishDemandExcel(String filePath) {
        // 设定Excel文件所在路径
        try {
            // 获取一个工作簿
            FileInputStream fileInputStream = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int firstRow = 1;
            // 获取sheet的最后一行
            int lastRow = sheet.getLastRowNum();

            for (int i = firstRow; i <= lastRow; i++) {
                XSSFRow row = sheet.getRow(i);
                int lastCol = row.getLastCellNum();
                XSSFCell cell = row.getCell(lastCol - 1);
                cell.setCellType(Cell.CELL_TYPE_STRING);// 强制作为String处理
                String invokable_relation = cell.getStringCellValue().trim();
                int[] funcIds = samlTypeService.getFuncIds(invokable_relation);
                row.createCell(lastCol).setCellValue(funcIds[0]);
                row.createCell(lastCol + 1).setCellValue(funcIds[1]);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);

            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *从源码生成设计数据
     * 1、递归扫描源码目录，获取所有的文件
     * 2、对于每一个文件，按照util包里的readTxtFile。2，3，4，5各自执行一遍
     */
    @Value("${import.design.fromLastWrong}")
    private boolean fromLastWrongImport; // 从上次出错位置继续导入设计

    @Override
    public void importDesignFromSource(String filePath, int pro) {
        File root = new File(filePath);

        List<String> filePaths = new ArrayList<>();
        filePaths = listAll(root, filePaths);
        for (int i = 0; i < filePaths.size(); i++) {
            String fp = filePaths.get(i);
            File fp_txt = new File(fp + ".txt");
            if (fromLastWrongImport) {
                if (!fp_txt.exists()) {
                    if (i > 0) {
                        i = i - 2; // 从没有txt的上一个开始import，因为最后一个有txt那个是出错的文件。
                    } else {
                        i = i - 1;
                    }
                    fromLastWrongImport = false;
                }
            } else {
                importFromOneFile(fp, filePath, pro);
            }
        }
    }

    /**
     * 给定一个文件的完整路径，解析。
     * fp是文件完整路径。
     * filePath是根目录，无斜杠。
     */
    @Override
    public void importFromOneFile(String file, String filePath, int pro) {
        System.out.println("当前正在解析的文件是：" + file);
        FileUtil.readFuncDef(file);
        FileUtil.distribLevelTxt(file + ".txt");
        String f2 = "_func_def.txt";
        String f3 = "_struct.txt";
        String f8 = "_include.txt";
        String f9 = "_func_declare.txt";
        String f13 = "_typedef.txt";
        String f14 = "_struct_declare.txt";
        String f12 = "_vars.txt";
        List<String> list2 = FileUtil.readTypeList(file + ".txt" + f2);
        List<String> list3 = FileUtil.readTypeList(file + ".txt" + f3);
        list2.addAll(list3);
        List<String> list8 = FileUtil.readTypeList(file + ".txt" + f8);
        list2.addAll(list8);
        List<String> list9 = FileUtil.readTypeList(file + ".txt" + f9);
        list2.addAll(list9);
        List<String> list13 = FileUtil.readTypeList(file + ".txt" + f13);
        list2.addAll(list13);
        List<String> list14 = FileUtil.readTypeList(file + ".txt" + f14);
        list2.addAll(list14);
        List<String> list12 = FileUtil.readTypeList(file + ".txt" + f12);
        list2.addAll(list12);
        samlTypeService.typeSave(list2, filePath, pro);
    }

    private List<String> listAll(File root, List<String> filePaths) {
        if (root.exists()) {
            File[] files = root.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].exists() && files[i].isDirectory()) {
                        listAll(files[i], filePaths);
                    } else {
                        String path = files[i].getAbsolutePath();
                        if (path.matches("(.*\\.h$)|(.*\\.c$)")) {
                            filePaths.add(path);
                        }
                    }
                }
            }
        }
        return filePaths;
    }

    /**
     * 把需求文件夹下的所有txt文件，导入到demand_path表中。
     * save的字段有：(Integer relationClassify, String name, String exp)
     *
     */
    @Override
    public void importDemandTxt(String dentryPath, int pro) {
        String[] files = FileUtil.getFilesName(dentryPath);// 没有path，纯文件名。
        for (int i = 0; i < files.length; i++) {// 遍历文件夹下的所有文件。
            String filePath = dentryPath + "/" + files[i];
            List<String> list = FileUtil.readFuncInvoke(filePath);

            //FileAgent fileAgent = new FileAgent(new ReadFuncInvoke());
            //List<String> list = fileAgent.readTxtFile(filePath);
            if (list == null || list.isEmpty()) continue;
            System.out.println(list.size());

            for (int j = 0; j < list.size(); j++) {
                String exp = list.get(j);
                String name = filePath + ";" + exp.split(";")[0];
                System.out.println(exp + "=======================");
                List<DemandInvoke> exist = demandInvokeDao.findByExpAndProject(exp, pro);
                if (exist.isEmpty()) {
                    DemandInvoke demandInvoke = new DemandInvoke(Data.Invocable, name, exp, pro);
                    demandInvokeDao.save(demandInvoke);
                }
            }
        }

    }

}
