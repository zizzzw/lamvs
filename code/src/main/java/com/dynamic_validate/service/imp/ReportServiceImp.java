package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.ErrorReportDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.dao.TypeLackDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.*;
import com.example.dynamic_validate.entity.*;
import com.dynamic_validate.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImp implements ReportService {
    @Autowired
    private TypeLackDao typeLackDao;
    @Autowired
    private ErrorReportDao errorReportDao;
    @Autowired
    private SamlTypeDao samlTypeDao;


    /**
     * 打印错误列表：List<ErrorClassify> errorList
     *
     */
    @Override
    public void printErrorList(List<ErrorClassify> errorList) {
        for (ErrorClassify e : errorList) {
            System.out.println(e.getId() + ":" + e.getErrorInfo());
        }
    }

    @Override
    public void saveError(ErrorReport old) {
        ErrorReport newerr = new ErrorReport(old.getTypeId(), old.getTypeName(), old.getTypeTable(), old.getErrorClassify(), old.getInfo(), old.getTitle(), old.getReportId());
        errorReportDao.save(newerr);
    }

    @Override
    public void saveError(String errClassify, Object type, int title, String report_id) {
        String typeTable = type.getClass().getSimpleName();
        String[] tmp = getTypeIdAndName(type);
        int typeId = Integer.parseInt(tmp[0]);
        String name = tmp[1];
        String info = getInfoAndSaveLack(typeTable, errClassify, name);

        errorReportDao.save(new ErrorReport(typeId, name, typeTable, errClassify, info, title, report_id));
    }

    @Override
    public void saveError(String errClassify, Object type, String info, String report_id) {
        if (info.equals("")) {
            saveError(errClassify, type, report_id);
        } else {
            String[] tmp = getTypeIdAndName(type);
            int typeId = Integer.parseInt(tmp[0]);
            String name = tmp[1];
            String typeTable = type.getClass().getSimpleName();
            //List<ErrorReport> list = errorReportDao.exist(name, typeTable, errClassify, report_id);
            //if (list == null || list.isEmpty()) {
            errorReportDao.save(new ErrorReport(typeId, name, typeTable, errClassify, info, report_id));
            //}
        }
    }

    @Override
    public void saveError(String errClassify, Object type, String report_id) {
        String typeTable = type.getClass().getSimpleName();
        String[] tmp = getTypeIdAndName(type);
        int typeId = Integer.parseInt(tmp[0]);
        String name = tmp[1];
        String info = getInfoAndSaveLack(typeTable, errClassify, name);
        errorReportDao.save(new ErrorReport(typeId, name, typeTable, errClassify, info, report_id));
    }

    private String getInfoAndSaveLack(String typeTable, String errClassify, String name) {
        String info = "";
        String[] tmp = name.split(":");
        long fatherId = 0;
        List<TypeLack> exist = new ArrayList<>();
        if (tmp.length == 1) {
            name = tmp[0];
        } else {
            name = tmp[1];
            fatherId = Long.parseLong(tmp[0]);
            exist = typeLackDao.findByNameAndErrClassifyAndFatherId(name, errClassify, fatherId);
        }

        switch (errClassify) {
            case Data.OK:
                info = Data.OK + ":当前类型" + name + "存在";
                break;
            case Data.Error:
                info = Data.Error + ":类型" + name + "错误";
                break;
            case Data.NullError:
                info = Data.NullError + ":传入类型为空";
                break;
            case Data.InvokeAccessError:
                info = Data.InvokeAccessError + ":关系不对，不能代入公式TR21，TR22，TR23，TR24";
                break;


            case Data.NotLevelTypeExist:
                info = Data.NotLevelTypeExist + ":非层次类型" + name + "存在";
                break;
            case Data.LackError:
                String fName = samlTypeDao.findSamlTypeById((int) fatherId).getName();
                info = Data.LackError + ":" + fName + "依赖类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;
            case Data.BaseExist:
                info = "(TR01)," + Data.BaseExist + ":基本类型" + name + "存在";
                break;
            case Data.BaseLackError:
                info = "(TR01)," + Data.BaseLackError + ":基本类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;
            case Data.FuncExist:
                info = "(TR02)," + Data.FuncExist + ":函数类型" + name + "存在";
                break;
            case Data.FuncLackError:
                info = "(TR02)," + Data.FuncLackError + ":函数类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;
            case Data.StructExist:
                info = "(TR03)," + Data.StructExist + ":结构体类型" + name + "存在";
                break;
            case Data.StructLackError:
                info = "(TR03)," + Data.StructLackError + ":结构体类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;
            case Data.FileExist:
                info = "(TR04)," + Data.FileExist + ":文件类型" + name + "存在";
                break;
            case Data.FileLackError:
                info = "(TR04)," + Data.FileLackError + ":文件类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;
            case Data.DentryExist:
                info = "(TR05)," + Data.DentryExist + ":目录类型" + name + "存在";
                break;
            case Data.DentryLackError:
                info = "(TR05)," + Data.DentryLackError + ":目录类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;
            case Data.SubsysExist:
                info = "(TR06)," + Data.SubsysExist + ":子系统类型" + name + "存在";
                break;
            case Data.SubsysLackError:
                info = "(TR06)," + Data.SubsysLackError + ":子系统类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;

            case Data.EnumExist:
                info = "(TR03)," + Data.EnumExist + ":Enum/Union类型" + name + "存在";
                break;
            case Data.EnumLackError:
                info = "(TR03)," + Data.EnumLackError + ":Enum/Union类型" + name + "不存在";
                break;

            case Data.ListExist:
                info = "(TR07)," + Data.ListExist + ":列表类型" + name + "存在";
                break;
            case Data.ListLackError:
                info = "(TR07)," + Data.ListLackError + ":列表类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;

            case Data.IncludeLackError:
                info = Data.IncludeLackError + ":当前include的文件" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;


            case Data.FuncParamLackError:
                info = "(p2)," + Data.FuncParamLackError + fatherId + ":函数参数类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }

                break;
            case Data.FuncReturnLackError:
                info = "(p2)," + Data.FuncReturnLackError + fatherId + ":函数返回值类型" + name + "不存在";
                if (!exist.isEmpty()) {
                    typeLackDao.updateById(exist.get(0).getId(), typeTable, fatherId, name, info, errClassify);
                } else {
                    typeLackDao.insert(typeTable, fatherId, name, info, errClassify);
                }
                break;


            case Data.BaseLevelError:
                info = Data.BaseLevelError + ":当前类型" + name + "不在base层";
                break;
            case Data.FuncLevelError:
                info = Data.FuncLevelError + ":当前类型" + name + "不在函数层";
                break;
            case Data.StructLevelError:
                info = Data.StructLevelError + ":当前类型" + name + "不在结构体层";
                break;
            case Data.FileLevelError:
                info = Data.FileLevelError + ":当前类型" + name + "不在文件层";
                break;
            case Data.DentryLevelError:
                info = Data.DentryLevelError + ":当前类型" + name + "不在目录层";
                break;
            case Data.SubsysLevelError:
                info = Data.SubsysLevelError + ":当前类型" + name + "不在子系统层";
                break;
            case Data.ParamLevelError:
                info = Data.ParamLevelError + ":当前类型" + name + "参数层次错误，不是Base|Func|Struct";
                break;
            case Data.VarLevelError:
                info = Data.VarLevelError + ":当前类型" + name + "变量类型层次错误，不是Base|Func|Struct";
                break;
            case Data.MethodLevelError:
                info = Data.MethodLevelError + ":当前类型" + name + "成员函数类型层次错误，不是Func";
                break;


            case Data.ParamInOK:
                info = "(TR11-1)," + Data.ParamInOK + ":入参关系正确" + name + "正确";
                break;
            case Data.ParamInError:
                info = "(TR11-1)," + Data.ParamInError + ":入参关系错误" + name + "错误";
                break;
            case Data.ParamOutOK:
                info = "(TR11-2)," + Data.ParamOutOK + ":出参关系" + name + "正确";
                break;
            case Data.ParamOutError:
                info = "(TR11-2)," + Data.ParamOutError + ":出参关系" + name + "错误";
                break;
            case Data.VarStructOK:
                info = "(TR12-1)," + Data.VarStructOK + ":成员变量关系" + name + "正确";
                break;
            case Data.VarStructError:
                info = "(TR12-1)," + Data.VarStructError + ":成员变量关系" + name + "错误";
                break;
            case Data.FuncStructOK:
                info = "(TR12-2)," + Data.FuncStructOK + ":成员函数关系" + name + "正确";
                break;
            case Data.FuncStructError:
                info = "(TR12-2)," + Data.FuncStructError + ":成员函数关系" + name + "错误";
                break;
            case Data.FuncFileOK:
                info = "(TR13-1)," + Data.FuncFileOK + ":全局函数关系" + name + "正确";
                break;
            case Data.FuncFileError:
                info = "(TR13-1)," + Data.FuncFileError + ":全局函数关系" + name + "错误";
                break;
            case Data.StructFileOK:
                info = "(TR13-2)," + Data.StructFileOK + ":结构体文件关系" + name + "正确";
                break;
            case Data.StructFileError:
                info = "(TR13-2)," + Data.StructFileError + ":结构体文件关系" + name + "错误";
                break;


            case Data.DemandOK:
                info = Data.DemandOK + ":需求正确";
                break;
            case Data.DemandError:
                info = Data.DemandError + ":需求错误";
                break;
            case Data.DemandPathOK:
                info = Data.DemandPathOK + ":需求路径" + name + "正确";
                break;
            case Data.DemandPathError:
                info = Data.DemandPathError + ":需求路径" + name + "错误";
                break;
            case Data.DemandInvokeOK:
                //info = Data.DemandInvokeOK + ":需求调用(2函数+依赖+调用)" + name + "正确";
                info = Data.DemandInvokeOK + ":需求调用" + name + "正确";
                break;
            case Data.DemandInvokeError:
                info = Data.DemandInvokeError + ":需求调用" + name + "错误";
                break;
            case Data.FuncDependOK:
                info = Data.FuncDependOK + ":函数" + name + "依赖类型均正确";
                break;
            case Data.FuncDependError:
                info = Data.FuncDependError + ":函数" + name + "依赖类型有错误";
                break;
            case Data.FuncInvokeOK:
                info = "(TR24)," + Data.FuncInvokeOK + ":函数可调用关系" + name + "正确";
                break;
            case Data.FuncInvokeError:
                info = "(TR24)," + Data.FuncInvokeError + ":函数可调用关系" + name + "错误";
                break;


            case Data.BaseOK:
                info = "(P1)," + Data.BaseOK + ":基本类型" + name + "正确";
                break;
            case Data.BaseError:
                info = "(P1)," + Data.BaseError + ":基本类型" + name + "错误";
                break;
            case Data.FuncOK:
                info = "(P2)," + Data.FuncOK + ":函数类型" + name + "正确";
                break;
            case Data.FuncError:
                info = "(P2)," + Data.FuncError + ":函数类型" + name + "错误";
                break;
            case Data.StructOK:
                info = "(P3)," + Data.StructOK + ":结构体类型" + name + "正确";
                break;
            case Data.StructError:
                info = "(P3)," + Data.StructError + ":结构体类型" + name + "错误";
                break;
            case Data.FileOK:
                info = "(P4)," + Data.FileOK + ":文件类型" + name + "正确";
                break;
            case Data.FileError:
                info = "(P4)," + Data.FileError + ":文件类型" + name + "错误";
                break;
            case Data.DentryOK:
                info = "(P5)," + Data.DentryOK + ":目录类型" + name + "正确";
                break;
            case Data.DentryError:
                info = "(P5)," + Data.DentryError + ":目录类型" + name + "错误";
                break;
            case Data.SubsysOK:
                info = "(P6)," + Data.SubsysOK + ":子系统类型" + name + "正确";
                break;
            case Data.SubsysError:
                info = "(P6)," + Data.SubsysError + ":子系统类型" + name + "错误";
                break;
            case Data.EnumOK:
                info = "(P3)," + Data.EnumOK + ":Enum/Union类型" + name + "正确";
                break;
            case Data.EnumError:
                info = "(P3)," + Data.EnumError + ":Enum/Union类型" + name + "错误";
                break;
            case Data.ListCompoundError:
                info = Data.ListCompoundError + ":列表类型" + name + "复合方式错误";
                break;
        }
        return info;
    }

    private String[] getTypeIdAndName(Object type) {
        String name = "";
        String typeId = "0";
        if (type instanceof SamlType) {
            SamlType o = (SamlType) type;
            typeId = o.getId() + "";
            name = o.getName();
        } else if (type instanceof SamlList) {
            SamlList o = (SamlList) type;
            typeId = o.getId() + "";
            name = o.getExp();
        } else if (type instanceof DemandInvoke) {
            DemandInvoke o = (DemandInvoke) type;
            typeId = o.getId() + "";
            name = o.getExp();
        } else if (type instanceof DemandPath) {
            DemandPath o = (DemandPath) type;
            typeId = o.getId() + "";
            name = o.getName();
        } else if (type instanceof SamlRelation) {
            SamlRelation o = (SamlRelation) type;
            typeId = o.getId() + "";
            name = o.getExp();
        } else if (type instanceof IncludeImport) {
            IncludeImport o = (IncludeImport) type;
            typeId = o.getId() + "";
            name = o.getExp();
        }
        String[] ss = {typeId, name};
        return ss;
    }

    @Override
    public List<List<ErrorReport>> findTitle2s(long s, long e, String repId) {
        List<List<ErrorReport>> list = new ArrayList<>();
        List<ErrorReport> l1 = errorReportDao.find2Func(s, e, repId);
        if (l1.isEmpty()) {
            System.out.println("l1是空的！");
        }
        List<ErrorReport> l2 = errorReportDao.findFuncDeps(s, e, repId);
        if (l2.isEmpty()) {
            System.out.println("l2是空的！");
        }
        List<ErrorReport> l3 = errorReportDao.findFuncInv(s, e, repId);
        if (l3.isEmpty()) {
            System.out.println("l3是空的！");
        }

        if (l1.isEmpty() && l2.isEmpty() && l3.isEmpty()) {
            return null;
        } else {
            list.add(l1);
            list.add(l2);
            list.add(l3);
            return list;
        }
    }

    public List<List<ErrorReport>> findTitle2s1(long diId, String repId) {
        List<List<ErrorReport>> list = new ArrayList<>();
        long min = errorReportDao.findMinId(repId);
        long s = min;
        long e = diId - 1;
        Long lastDid = errorReportDao.findLastDId(diId, repId);
        if (lastDid != null && lastDid > min) {
            s = lastDid + 1;
        }
        List<ErrorReport> l1 = errorReportDao.find2Func(s, e, repId);
        if (l1 == null || l1.isEmpty()) {
            System.out.println("l1是空的！");
        }
        List<ErrorReport> l2 = errorReportDao.findFuncDeps(s, e, repId);
        if (l2.isEmpty()) {
            System.out.println("l2是空的！");
        }
        List<ErrorReport> l3 = errorReportDao.findFuncInv(s, e, repId);
        if (l3.isEmpty()) {
            System.out.println("l3是空的！");
        }
        if (l1.isEmpty() && l2.isEmpty() && l3.isEmpty()) {
            return null;
        } else {
            list.add(l1);
            list.add(l2);
            list.add(l3);
            return list;
        }
    }

    @Override
    public List<List<ErrorReport>> findTitle3s(long s, long e, String repId) {
        List<List<ErrorReport>> list = new ArrayList<>();

        List<ErrorReport> l1 = errorReportDao.findRightType(s, e, repId);
        if (l1.isEmpty()) {
            System.out.println("l1是空的！");
        }

        List<ErrorReport> l2 = errorReportDao.findWrongType(s, e, repId);
        if (l2.isEmpty()) {
            System.out.println("l2是空的！");
        }

        if (l1.isEmpty() && l2.isEmpty()) {
            return null;
        } else {
            list.add(l1);
            list.add(l2);
            return list;
        }
    }

    @Override
    public List<List<ErrorReport>> findTitle4s(long s, long e, String repId) {
        List<List<ErrorReport>> list = new ArrayList<>();

        List<ErrorReport> l1_1 = errorReportDao.findRightRela(s, e, repId);
        if (l1_1.isEmpty()) {
            System.out.println("l1Right是空的！");
        }
        List<ErrorReport> l1_2 = errorReportDao.findWrongRela(s, e, repId);
        if (l1_2.isEmpty()) {
            System.out.println("l1Wrong是空的！");
        }

        List<ErrorReport> l2_1 = errorReportDao.findRightTypeEx(s, e, repId);
        if (l2_1.isEmpty()) {
            System.out.println("l2Right是空的！");
        }
        List<ErrorReport> l2_2 = errorReportDao.findWrongTypeEx(s, e, repId);
        if (l2_2.isEmpty()) {
            System.out.println("l2Wrong是空的！");
        }

        if (l1_1.isEmpty() && l1_2.isEmpty() && l2_1.isEmpty() && l2_2.isEmpty()) {
            return null;
        } else {
            list.add(l1_1);
            list.add(l1_2);
            list.add(l2_1);
            list.add(l2_2);
            return list;
        }
    }

    @Override
    public void generReportStart(String repId) {
        List<ErrorReport> dis = errorReportDao.findDemand(repId);
        for (int i = 0; i < dis.size(); i++) {
            long id1 = dis.get(i).getId();
            long s1 = errorReportDao.findMinId(repId);
            if (i > 0) {
                s1 = dis.get(i - 1).getId() + 1;
            }
            errorReportDao.updateStartId(id1, s1);

            List<ErrorReport> tt2s = errorReportDao.findTitle2(s1, id1, repId);
            if (tt2s.isEmpty()) {
                List<ErrorReport> tt3s = errorReportDao.findType(s1, id1, repId);
                if (tt3s.isEmpty()) {
                    List<ErrorReport> tt4s = errorReportDao.findTitle4(s1, id1, repId);
                    for (int l = 0; l < tt4s.size(); l++) {
                        long id4 = tt4s.get(l).getId();
                        long s4 = s1;
                        if (l > 0) {
                            s4 = tt4s.get(l - 1).getId() + 1;
                        }
                        errorReportDao.updateStartId(id4, s4);
                    }

                } else {
                    for (int k = 0; k < tt3s.size(); k++) {
                        long id3 = tt3s.get(k).getId();
                        long s3 = s1;
                        if (k > 0) {
                            s3 = tt3s.get(k - 1).getId() + 1;
                        }
                        errorReportDao.updateStartId(id3, s3);

                        List<ErrorReport> tt4s = errorReportDao.findTitle4(s3, id3, repId);
                        for (int l = 0; l < tt4s.size(); l++) {
                            long id4 = tt4s.get(l).getId();
                            long s4 = s3;
                            if (l > 0) {
                                s4 = tt4s.get(l - 1).getId() + 1;
                            }
                            errorReportDao.updateStartId(id4, s4);
                        }

                    }
                }
            } else {
                for (int j = 0; j < tt2s.size(); j++) {
                    long id2 = tt2s.get(j).getId();
                    long s2 = s1;
                    if (j > 0) {
                        s2 = tt2s.get(j - 1).getId() + 1;
                    }
                    errorReportDao.updateStartId(id2, s2);

                    List<ErrorReport> tt3s = errorReportDao.findType(s2, id2, repId);
                    if (tt3s.isEmpty()) {
                        List<ErrorReport> tt4s = errorReportDao.findTitle4(s2, id2, repId);
                        for (int l = 0; l < tt4s.size(); l++) {
                            long id4 = tt4s.get(l).getId();
                            long s4 = s2;
                            if (l > 0) {
                                s4 = tt4s.get(l - 1).getId() + 1;
                            }
                            errorReportDao.updateStartId(id4, s4);
                        }

                    }
                    for (int k = 0; k < tt3s.size(); k++) {
                        long id3 = tt3s.get(k).getId();
                        long s3 = s2;
                        if (k > 0) {
                            s3 = tt3s.get(k - 1).getId() + 1;
                        }
                        errorReportDao.updateStartId(id3, s3);

                        List<ErrorReport> tt4s = errorReportDao.findTitle4(s3, id3, repId);
                        for (int l = 0; l < tt4s.size(); l++) {
                            long id4 = tt4s.get(l).getId();
                            long s4 = s3;
                            if (l > 0) {
                                s4 = tt4s.get(l - 1).getId() + 1;
                            }
                            errorReportDao.updateStartId(id4, s4);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String generRepTxt(String repId, List<ErrorReport> diList) {
        String file = Data.RepTxt + repId + ".txt";
        PrintStream ps = null;

        try {
            ps = new PrintStream(file);
            System.setOut(ps); // 设置输出到文件，就不再后台打印了。

            // 每一个demand要对应一群其他的
            long s = errorReportDao.findMinId(repId) - 1;
            long e = s;
            String tab = "\t";
            String tab2 = "\t\t";
            String tab3 = "\t\t\t";
            for (ErrorReport d : diList) {
                //String tt1 = "需求调用：" + d.getTypeName();
                //System.out.println(tt1 + ":" + d.getInfoAndSaveLack());
                System.out.println(d.getId() + "," + d.getInfo());

                s = e + 1;
                e = d.getId() - 1;
                String[] f12 = d.getTypeName().split(";");
                String f1 = f12[0];
                String f2 = f12[1];

                List<ErrorReport> tt21s = errorReportDao.find2Func(s, e, repId); //两个函数的正确性，存的是2个函数
                if (!tt21s.isEmpty()) {
                    //String tt21 = tab + "主调函数" + f1 + "和被调函数" + f2 + "的正确性";
                    String tt21 = tab + "====两函数的正确性====";
                    System.out.println(tt21);
                }
                long tt21is = s - 1;
                long tt21ie = tt21is;
                for (ErrorReport t21i : tt21s) {
                    //String tt21_i = tab + t21i.getTypeName();
                    //System.out.println(tt21_i + ":" + t21i.getInfoAndSaveLack());
                    System.out.println(tab + t21i.getId() + "," + t21i.getInfo());
                    tt21is = tt21ie + 1;
                    tt21ie = t21i.getId() - 1;
                    //System.out.println(tab3 + "====关系存在性：====");
                    List<ErrorReport> tt41s = errorReportDao.findRela(tt21is, tt21ie, repId); //函数正确性验证过程中，关系，参数、返回值存在性，层次正确性。
                    for (ErrorReport t41 : tt41s) {
                        //String t41_i = tab3 + t41.getTypeName();
                        //System.out.println(t41_i + ":" + t41.getInfoAndSaveLack());
                        System.out.println(tab3 + t41.getId() + "," + t41.getInfo());
                    }
                    //System.out.println(tab3 + "====类型存在性：====");
                    List<ErrorReport> tt42s = errorReportDao.findTypeEx(tt21is, tt21ie, repId); //函数正确性验证过程中，关系，参数、返回值存在性，层次正确性。
                    for (ErrorReport t42 : tt42s) {
                        //String t42_i = tab3 + t42.getTypeName();
                        //System.out.println(t42_i + ":" + t42.getInfoAndSaveLack());
                        System.out.println(tab3 + t42.getId() + "," + t42.getInfo());
                    }
                }


                List<ErrorReport> tt22s = errorReportDao.findFuncDeps(s, e, repId); // 两个函数的依赖类型集合正确性，存的是2个函数
                if (!tt22s.isEmpty()) {
                    //String tt22 = tab + f1 + "和" + f2 + "的依赖类型正确性";
                    String tt22 = tab + "====两函数的依赖类型正确性====";
                    System.out.println(tt22);
                }
                long tt22is = tt21ie;
                long tt22ie = tt22is;
                for (ErrorReport t22i : tt22s) {
                    //String tt22_i = tab + t22i.getTypeName();
                    //System.out.println(tt22_i + ":" + t22i.getInfoAndSaveLack());
                    System.out.println(tab + t22i.getId() + "," + t22i.getInfo());
                    tt22is = tt22ie + 1;
                    tt22ie = t22i.getId() - 1;
                    List<ErrorReport> tt3s = errorReportDao.findType(tt22is, tt22ie, repId); //函数正确性验证过程中，类型正确性。

                    long tt3is = tt22is - 1;
                    long tt3ie = tt3is;
                    for (ErrorReport t3i : tt3s) {
                        //String tt3_i = tab2 + t3i.getTypeName();
                        //System.out.println(tt3_i + ":" + t3i.getInfoAndSaveLack());
                        System.out.println(tab2 + t3i.getId() + "," + t3i.getInfo());
                        tt3is = tt3ie + 1;
                        tt3ie = t3i.getId() - 1;
                        List<ErrorReport> tt41s = errorReportDao.findRela(tt3is, tt3ie, repId); //函数正确性验证过程中，关系，参数、返回值存在性，层次正确性。
                        if (!tt41s.isEmpty()) {
                            System.out.println(tab3 + "====关系存在性====");
                        }
                        for (ErrorReport t41 : tt41s) {
                            //String t41_i = tab3 + t41.getTypeName();
                            //System.out.println(t41_i + ":" + t41.getInfoAndSaveLack());
                            System.out.println(tab3 + t41.getId() + "," + t41.getInfo());
                        }
                        List<ErrorReport> tt42s = errorReportDao.findTypeEx(tt3is, tt3ie, repId); //函数正确性验证过程中，关系，参数、返回值存在性，层次正确性。
                        if (!tt42s.isEmpty()) {
                            System.out.println(tab3 + "====类型存在性====");
                        }
                        for (ErrorReport t42 : tt42s) {
                            //String t42_i = tab3 + t42.getTypeName();
                            //System.out.println(t42_i + ":" + t42.getInfoAndSaveLack());
                            System.out.println(tab3 + t42.getId() + "," + t42.getInfo());
                        }

                    }

                }

                List<ErrorReport> tt23s = errorReportDao.findFuncInv(s, e, repId); // 函数是否可调用，存的是DemandInvoke
                if (!tt23s.isEmpty()) {
                    //String tt23 = tab + f1 + "--≪Invokable≫-->" + f2 + "可调用关系的正确性";
                    String tt23 = tab + "====可调用关系====";
                    System.out.println(tt23);
                }
                for (ErrorReport t23 : tt23s) {
                    //String t23_i = tab2 + t23.getTypeName();
                    //System.out.println(t23_i + ":" + t23.getInfoAndSaveLack());
                    System.out.println(tab2 + t23.getId() + "," + t23.getInfo());
                }
            }
            ps.close();
            return file;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            ps.close();
            return "";
        }
    }


    public List<List<ErrorReport>> findTitle3s1(int diId, String repId) {
        List<List<ErrorReport>> list = new ArrayList<>();
        //int min = errorReportDao.findMinId(repId);
        long s = 0;
        long e = diId - 1;
        Long lastDid = errorReportDao.findLastDId(diId, repId);
        if (lastDid != null && lastDid > s) {
            s = lastDid + 1;
        }

        List<ErrorReport> l1_23 = new ArrayList<>();
        List<ErrorReport> l1 = errorReportDao.find2Func(s, e, repId);
        if (l1.isEmpty()) {
            System.out.println("l1是空的！");
        } else {
            long tt21is = s - 1;
            long tt21ie = tt21is;
            for (ErrorReport t21i : l1) {
                l1_23.add(t21i); // 把两个函数加入
                tt21is = tt21ie + 1;
                tt21ie = t21i.getId() - 1;
                //System.out.println(tab3 + "====关系存在性：====");
                //List<ErrorReport> tt41s = errorReportDao.findRela(tt21is, tt21ie, repId); //函数正确性验证过程中，关系，参数、返回值存在性，层次正确性。
                //for (ErrorReport t41 : tt41s) {
                //    String t41_i = tab3 + t41.getTypeName();
                //    System.out.println(t41_i + ":" + t41.getInfoAndSaveLack());
                //}
                //System.out.println(tab3 + "====类型存在性：====");
                //List<ErrorReport> tt42s = errorReportDao.findTypeEx(tt21is, tt21ie, repId); //函数正确性验证过程中，关系，参数、返回值存在性，层次正确性。
                //for (ErrorReport t42 : tt42s) {
                //    String t42_i = tab3 + t42.getTypeName();
                //    System.out.println(t42_i + ":" + t42.getInfoAndSaveLack());
                //}

            }


        }
        list.add(l1);
        List<ErrorReport> l2 = errorReportDao.findFuncDeps(s, e, repId);
        if (l2.isEmpty()) {
            System.out.println("l2是空的！");
        }
        list.add(l2);
        List<ErrorReport> l3 = errorReportDao.findFuncInv(s, e, repId);
        if (l3.isEmpty()) {
            System.out.println("l3是空的！");
        }
        list.add(l3);

        return list;
    }

}
