package com.dynamic_validate.service;

import com.dynamic_validate.entity.ErrorClassify;
import com.dynamic_validate.entity.ErrorReport;

import java.util.List;

public interface ReportService {

    void printErrorList(List<ErrorClassify> errorList);

    void saveError(ErrorReport old);

    void saveError(String errClassify, Object type, int title, String report_id);

    void saveError(String errClassify, Object type, String info, String report_id);

    void saveError(String errClassify, Object type, String report_id);

    List<List<ErrorReport>> findTitle2s(long s, long e, String repId);

    List<List<ErrorReport>> findTitle3s(long s, long e, String repId);

    List<List<ErrorReport>> findTitle4s(long s, long e, String repId);

    /**
     *
     * 验证之后先完善一下start位置index。
     * 也就是本条的上一条-1。
     */
    void generReportStart(String repId);

    /**
     * 产生报告TXT文件，可以下载。
     * 设置输出到文件，就不再后台打印了。
     * 返回文件名，如果输出错误，就返回""。
     */
    String generRepTxt(String repId, List<ErrorReport> diList);
}
