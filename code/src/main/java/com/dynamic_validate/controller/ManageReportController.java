package com.dynamic_validate.controller;

import com.dynamic_validate.dao.ErrorReportDao;
import com.dynamic_validate.entity.ErrorReport;
import com.dynamic_validate.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class ManageReportController {
    @Autowired
    private ErrorReportDao errorReportDao;
    @Autowired
    private ReportService reportService;


    @GetMapping("/showAllReport")
    @ResponseBody
    public Map<String, Object> showAllReport(@RequestParam(value = "pageIndex") int pageIndex, @RequestParam(value = "pageSize") int pageSize) {
        Map<String, Object> map = new HashMap<>();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<ErrorReport> reportPage = errorReportDao.findAll(pageable);

        System.out.println(pageIndex + "=========目标页面");
        System.out.println(reportPage);
        map.put("reportPage", reportPage);

        return map;
    }

    @GetMapping("/showReport")
    @ResponseBody
    public Map<String, Object> showReport(@RequestParam(value = "repId") String repId, @RequestParam(value = "pageIndex") int pageIndex, @RequestParam(value = "pageSize") int pageSize) {
        Map<String, Object> map = new HashMap<>();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<ErrorReport> reportPage;
        System.out.println(repId + "...................");
        if (repId.equals("-1")) {
            reportPage = errorReportDao.findAll(pageable);
        } else {
            reportPage = errorReportDao.findByReportId(repId, pageable);
        }
        System.out.println(pageIndex + "=========目标页面");
        System.out.println(reportPage);
        map.put("reportPage", reportPage);

        return map;
    }

    @GetMapping("/showProcess")
    public String showProcess(@RequestParam(value = "repId") String repId, @RequestParam(value = "diId") int diId, Model model) {
        long e = diId; // 这里不减1了，带上当前的。
        long s = errorReportDao.findById(diId).getStartId();
        List<ErrorReport> showProcess = errorReportDao.findProcess(s, e, repId);
        model.addAttribute("showProcess", showProcess);

        model.addAttribute("repId", repId);
        return "veri_reportTitle2";
    }

    @GetMapping("/showTitle4")
    public String showTitle4(@RequestParam(value = "repId") String repId, @RequestParam(value = "diId") int diId, Model model) {
        long s = errorReportDao.findById(diId).getStartId();
        long e = diId - 1;
        List<List<ErrorReport>> showTitle4 = reportService.findTitle4s(s, e, repId);
        if (showTitle4 == null) { // 没有，应该查找process的
            System.out.println("tt4=null");
            showProcess(repId, diId, model);
            //List<ErrorReport> showProcess = errorReportDao.findProcess(s, e, repId);
            //model.addAttribute("showProcess", showProcess);
        } else {
            model.addAttribute("showTitle4", showTitle4);
            model.addAttribute("repId", repId);
        }

        return "veri_reportTitle2";
    }

    @GetMapping("/showTitle3")
    public String showTitle3(@RequestParam(value = "repId") String repId, @RequestParam(value = "diId") int diId, Model model) {
        long s = errorReportDao.findById(diId).getStartId();
        long e = diId - 1;
        List<List<ErrorReport>> showTitle3 = reportService.findTitle3s(s, e, repId);
        if (showTitle3 == null) { // 没有，应该查找title3的
            System.out.println("tt3=null");
            showTitle4(repId, diId, model);
            //List<List<ErrorReport>> showTitle4 = reportService.findTitle4s(s, e, repId);
            //if (showTitle4 == null) { // 没有，应该查找title3的
            //    List<ErrorReport> showProcess = errorReportDao.findProcess(s, e, repId);
            //    model.addAttribute("showProcess", showProcess);
            //} else {
            //    model.addAttribute("showTitle4", showTitle4);
            //}
        } else {
            model.addAttribute("showTitle3", showTitle3);
            model.addAttribute("repId", repId);
        }
        return "veri_reportTitle2";
    }

    @GetMapping("/openTitle3")
    @ResponseBody
    public Map<String, Object> openTitle3(@RequestParam(value = "repId") String repId, @RequestParam(value = "diId") int diId, Model model) {
        Map<String, Object> map = new HashMap<>();

        long s = errorReportDao.findById(diId).getStartId();
        long e = diId - 1;
        List<List<ErrorReport>> showTitle3 = reportService.findTitle3s(s, e, repId);
        if (showTitle3 == null) { // 没有，应该查找title3的
            System.out.println("tt3=null");
            showTitle4(repId, diId, model);
        } else {
            map.put("showTitle3", showTitle3);
        }
        return map;
    }

    @GetMapping("/showTitle2")
    public String showTitle2(@RequestParam(value = "repId") String repId, @RequestParam(value = "diId") int diId, Model model) {
        long s = errorReportDao.findById(diId).getStartId();
        long e = diId - 1;
        List<List<ErrorReport>> showTitle2 = reportService.findTitle2s(s, e, repId);
        if (showTitle2 == null) { // 没有，应该查找title3的
            System.out.println("tt2=null");
            showTitle3(repId, diId, model);

            //List<List<ErrorReport>> showTitle3 = reportService.findTitle3s(s, e, repId);
            //if (showTitle3 == null) { // 没有，应该查找title3的
            //    List<List<ErrorReport>> showTitle4 = reportService.findTitle4s(s, e, repId);
            //    if (showTitle4 == null) { // 没有，应该查找title3的
            //        List<ErrorReport> showProcess = errorReportDao.findProcess(s, e, repId);
            //        model.addAttribute("showProcess", showProcess);
            //    } else {
            //        model.addAttribute("showTitle4", showTitle4);
            //    }
            //} else {
            //    model.addAttribute("showTitle3", showTitle3);
            //}
        } else {
            model.addAttribute("showTitle2", showTitle2);
            model.addAttribute("repId", repId);
        }

        return "veri_reportTitle2";
    }


    @GetMapping("/showRepDemand")
    @ResponseBody
    public Map<String, Object> showRepDemand(@RequestParam(value = "repId") String repId, @RequestParam(value = "pageIndex") int pageIndex, @RequestParam(value = "pageSize") int pageSize) {
        Map<String, Object> map = new HashMap<>();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<ErrorReport> repDemandPage;
        List<ErrorReport> diList = errorReportDao.findDemand(repId);
        System.out.println(repId + "...................");
        if (repId.equals("-1")) {
            repDemandPage = errorReportDao.findAll(pageable);
        } else {
            repDemandPage = new PageImpl<>(diList, pageable, diList.size());
        }
        System.out.println(pageIndex + "=========目标页面");
        System.out.println(repDemandPage);
        map.put("repDemandPage", repDemandPage);

        String file = reportService.generRepTxt(repId, diList);
        map.put("file", file);

        return map;
    }


}
