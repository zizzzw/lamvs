package com.dynamic_validate.data;

import java.io.File;

public class Data {
    public static final int SubType = 1;//1.SubType 子类型
    public static final int Aggregation = 2;//2.Aggregation 聚合
    public static final int TypeAssociation = 3;//3.TypeAssociation 类关联
    public static final int ParamIn = 4;//4.ParamIn 输入参数关联
    public static final int ParamOut = 5;//5.ParamOut 输出参数关联
    public static final int VarStruct = 6;//6.VarStruct 成员变量关联
    public static final int FuncStruct = 7;//7.FuncStruct 成员函数关联，函数结构体关联
    public static final int Accessible = 8;//8.Accessible 可访问
    public static final int Invocable = 9;//9.Invocable 函数可调用
    public static final int ParamInvocable = 10;//10.ParamInvocable 可传参调用
    public static final int FuncFile = 11;//11.FuncFile 全局函数文件关联
    public static final int StructFile = 12;//12.StructFile 结构体文件关联
    public static final int ListAgg = 13; // 13. 列表聚合成层次类型


    public static final int BASE = 1;//1	base	C语言基础类型
    public static final int FUNC = 2;//2	function	函数
    public static final int STRUCT = 3;//3	struct	结构体（类/接口）
    public static final int FILE = 4;//4	file	.h或.c文件
    public static final int DENTRY = 5;//5	dentry	目录，根目录到.h或.c文件之间的目录
    public static final int SUBSYS = 6;//6	subsystem	子系统（Linux内核顶层目录）
    public static final int MACRO = 7;//7	macro	宏，暂时忽略
    public static final int INCLUDE = 8;//8	include	文件中的include文件集合
    public static final int FUNC_DECLARE = 9;//9	文件中的函数声明，包括extern
    public static final int ENUM = 10;//10	枚举类型
    public static final int UNION = 11;//11	共用体
    public static final int VARS = 12;//12	变量
    public static final int TYPEDEF = 13;//13	typedef
    public static final int STRUCT_DECLARE = 14;//14 结构体声明
    public static final int PARA_FUNC_DECLARE = 15;//15	参数位置的函数声明


    public static final int Pro6 = 6;// 6	open	open流程局部
    public static final int Pro7 = 7;// 7	pguest	内核分区
    public static final int Pro8 = 8;// 8	phost	内核分区
    public static final int Pro9 = 9;// 9	fs_pguestfs	  guest的文件系统

    public static final int State = 1;// 用户没删除
    public static final int Valid0 = 0;// 验证错误
    public static final int Valid1 = 1;// 验证正确
    public static final int Valid2 = 2;// 未验证


    public static final int field2List = 1;// relation表中的field2是List，field2_classify为1
    public static final int field2Type = 0;// relation表中的field2是Type，field2_classify为0
    public static final String WinNewLine = "\r\n";

    public static final String Error = "-1_2";
    public static final String OK = "200_1";
    public static final String LevelTypeExist = "030_1";
    public static final String NotLevelTypeExist = "031_1";
    public static final String LackError = "020_2";
    public static final String BaseExist = "001_1";
    public static final String BaseLackError = "001_2";
    public static final String FuncExist = "002_1";
    public static final String FuncLackError = "002_2";
    public static final String StructExist = "003_1";
    public static final String StructLackError = "003_2";
    public static final String FileExist = "004_1";
    public static final String FileLackError = "004_2";
    public static final String DentryExist = "005_1";
    public static final String DentryLackError = "005_2";
    public static final String SubsysExist = "006_1";
    public static final String SubsysLackError = "006_2";
    public static final String ListExist = "007_1";
    public static final String ListLackError = "007_2";
    public static final String IncludeLackError = "008_2";
    public static final String EnumExist = "010_1";
    public static final String EnumLackError = "010_2";


    public static final String FuncParamLackError = "101_2"; //参数的类型找不到
    public static final String FuncReturnLackError = "102_2"; //返回值的类型找不到


    public static final String BaseLevelError = "201";
    public static final String FuncLevelError = "202";
    public static final String StructLevelError = "203";
    public static final String FileLevelError = "204";
    public static final String DentryLevelError = "205";
    public static final String SubsysLevelError = "206";
    public static final String ParamLevelError = "210";
    public static final String ReturnLevelError = "211";
    public static final String VarLevelError = "212";
    public static final String MethodLevelError = "213";


    public static final String ParamInOK = "301_1";
    public static final String ParamInError = "301_2";
    public static final String ParamOutOK = "302_1";
    public static final String ParamOutError = "302_2";
    public static final String VarStructOK = "303_1";
    public static final String VarStructError = "303_2";
    public static final String FuncStructOK = "304_1";
    public static final String FuncStructError = "304_2";
    public static final String FuncFileOK = "305_1";
    public static final String FuncFileError = "305_2";
    public static final String StructFileOK = "306_1";
    public static final String StructFileError = "306_2";

    public static final int DITitle = 1;       // 1表示需求调用(2函数+依赖+调用)402，
    public static final int FuncTitle = 21;    // 21表示函数f1和f2的验证502，
    public static final int FuncDepTitle = 22; // 22表示f1或f2全部依赖类型403,
    public static final int FuncInvTitle = 23; // 23表示仅调用404，
    public static final int TypeTitle = 3;     // 3表示验证类型结构正确性50X，
    public static final int RelaTitle = 41;    // 41表示关系30X，
    public static final int TypeExTitle = 42;  // 42表示存在性00X和10X，
    public static final int ProTitle = 0;      // 0表示其他过程性问题，默认就是
    public static final int DTitle = 10;      // 10表示最高级别的标题，Demand，目前用不到
    public static final int DPTitle = 11;      // 11表示次高级别的标题，DemandPath，目前用不到

    public static final String Demand = "400";
    public static final String DemandOK = "400_1";
    public static final String DemandError = "400_2";
    public static final String DemandPath = "401";
    public static final String DemandPathOK = "401_1";
    public static final String DemandPathError = "401_2";
    public static final String DemandInvoke = "402";  // (2函数+依赖+调用)
    public static final String DemandInvokeOK = "402_1";
    public static final String DemandInvokeError = "402_2";
    public static final String FuncDepend = "403";
    public static final String FuncDependOK = "403_1";
    public static final String FuncDependError = "403_2";
    public static final String FuncInvoke = "404";
    public static final String FuncInvokeOK = "404_1";
    public static final String FuncInvokeError = "404_2";

    public static final String Base = "501";
    public static final String BaseOK = "501_1";
    public static final String BaseError = "501_2";
    public static final String Func = "502";
    public static final String FuncOK = "502_1";
    public static final String FuncError = "502_2";
    public static final String Struct = "503";
    public static final String StructOK = "503_1";
    public static final String StructError = "503_2";
    public static final String File = "504";
    public static final String FileOK = "504_1";
    public static final String FileError = "504_2";
    public static final String Dentry = "505";
    public static final String DentryOK = "505_1";
    public static final String DentryError = "505_2";
    public static final String Subsys = "506";
    public static final String SubsysOK = "506_1";
    public static final String SubsysError = "506_2";
    public static final String ListCompoundError = "507";
    public static final String Enum = "510";
    public static final String EnumOK = "510_1";
    public static final String EnumError = "510_2";

    public static final String NullError = "601";
    public static final String InvokeAccessError = "602";

    public static final String TR1_Error = "700";
    public static final String TR1_Error1 = "701"; //<f_1,f_2 都是函数>错误
    public static final String TR1_Error2 = "702"; //f_1.scope≤f_2.scope错误


    public static final String[] ErrorsByLevel = {"200", "001", "002", "003", "004", "005", "006"};
    public static final String[] TypeTable = {"SamlType", "SamlList", "DemandInvoke", "SamlRelation"};


    public static final String funcIgnoreWord = "struct|union|enum|extern|const|static|final|inline|notrace|__always_inline|__bitwise__|__bitwise|__safe|__force|__nocast|__must_check|__attribute___|__iomem|__user|__exit|__printf(1,2)|__init|__releases(files->file_lock)|__acquires(files->file_lock)|__cacheline_aligned_in_smp|__cacheline_aligned";
    public static final String funcIgnoreWordBegin = "struct|union|enum|extern|const|static|final|inline";
    public static final String structPreIgnoreWord = "extern|const";


    public static final String demandExcel = "G:/研究生/毕设/我的毕设/系统实现/checkSys数据/6-导入数据（同数据库格式和id）/saml_demand.xlsx";
    //public static final String demandDentry = "G:\\研究生\\毕设\\我的毕设\\系统实现\\checkSys数据\\8-最新数据\\待处理txt调用";// 需求txt文件所在目录
    //public static final String demand = "G:\\研究生\\毕设\\我的毕设\\0小论文\\王梓资料\\数据\\";
    public static String rootPath = System.getProperty("user.dir");
    public static String rootPathParent = new File(rootPath).getParent() + "\\";
    public static final String demand = rootPathParent + "data\\demand\\";

    public static final String d_vfs = demand + "d_vfs";// 需求txt文件所在目录
    public static final String d_guest = demand + "d_guest";// 需求txt文件所在目录
    public static final String d_host = demand + "d_host";// 需求txt文件所在目录
    public static final String fs_pguestfs = demand + "fs-pguestfs";// 需求txt文件所在目录
    public static final String source = rootPathParent + "data\\source\\";// 需求txt文件所在目录
    public static final String source_linux = source + "linux";// 需求txt文件所在目录
    public static final String source_guest = source + "guest_small";// 需求txt文件所在目录
    //public static final String source_guest = source + "guest";// 需求txt文件所在目录
    //public static final String source_guest = source+"test_guest1";// 需求txt文件所在目录
    public static final String source_host = source + "host";// 需求txt文件所在目录

    public static final String copy_sour = "G:\\c_files\\VSCode\\2022-4-10code\\";
    public static final String copy_sour_linux = copy_sour + "linux.tar\\linux";
    public static final String copy_sour_guest = copy_sour + "partition-kernel\\partition-guest";
    public static final String copy_sour_host = copy_sour + "partition-kernel\\partition-host";

    public static final String paramFuncReg = "(,?\\s*\\(\\*.*\\(.*\\)\\(.*\\).*\\),?)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?【不，这个有问题】。
    public static final String pointerReg = "^;?[\\w\\s\\*]+\\(\\s*\\*\\s*\\w+\\s*\\)\\s*\\(.*\\);?\\s*$"; // 参数表前有(*函数名)
    public static final String funcReg = "[\\w\\s]*\\([\\w\\s,]*\\);?\\s*$";

    //public static final String unionEnumStructReg = "(union[\\w\\s]*\\{[\\w\\s\\*;]*\\};?)|(enum[\\w\\s]*\\{[\\w\\s\\*;]*\\};?)|(struct[\\w\\s]*\\{[\\w\\s\\*;]*\\};?)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?。
    //public static final String unionReg = "(;?\\s*union[\\w\\s]*\\{[\\w\\s\\*;]*\\};?)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?。
    //public static final String enumReg = "(;?\\s*enum[\\w\\s]*\\{[\\w\\s\\*;]*\\};?)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?。
    //public static final String structReg = "(;?\\s*struct[\\w\\s]*\\{[\\w\\s\\*;]*\\};?)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?。
    public static final String unionEnumStructReg = "(union[\\w\\s]*\\{[\\w\\s\\*;,\\(\\)\\[\\]]*\\};?)|(enum[\\w\\s]*\\{[\\w\\s\\*;,\\(\\)\\[\\]]*\\};?)|(struct[\\w\\s]*\\{[\\w\\s\\*;,\\(\\)\\[\\]]*\\};?)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?。
    public static final String unionReg = "(;?\\s*union[\\w\\s]*\\{[\\w\\s\\*;,\\(\\)\\[\\]]*\\};?)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?。
    public static final String enumReg = "(;?\\s*enum[\\w\\s]*\\{[\\w\\s\\*;,\\(\\)\\[\\]]*\\};?)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?。
    public static final String structReg = "(;?\\s*struct[\\w\\s]*\\{[\\w\\s\\*;,\\(\\)\\[\\]]*\\};?)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?。


    public static final String RepTxt = System.getProperty("user.dir") + "/rep_"; //报告所存TXT文件前缀，后面加上repId.txt即可
    public static String repId;

}
