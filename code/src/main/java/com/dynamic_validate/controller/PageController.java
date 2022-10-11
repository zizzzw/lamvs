package com.dynamic_validate.controller;

import com.dynamic_validate.dao.ErrorReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class PageController {
    @Autowired
    private ErrorReportDao errorReportDao;

    @GetMapping({"/", "personality"})
    public String index() {
        return "personality";
    }

    @GetMapping({"/login", "/user/login"})
    public String login() {
        return "login";
    }

    @GetMapping("/m_header")
    public String m_header() {
        return "m_header";
    }

    @GetMapping("/foot")
    public String foot() {
        return "foot";
    }

    //直接进入type_manage.html页面，进去之后再通过Ajax异步请求所有的type分页数据。
    //在TypeController中有，这里再写就重复了。
    // @GetMapping("/typeManage")
    // public String typeManage() {
    //     return "type_manage";
    // }

    @GetMapping("/m_type")
    public String m_type() {
        return "m_type";
    }

    @GetMapping("/m_list")
    public String m_list() {
        return "m_list";
    }

    @GetMapping("/m_relation")
    public String m_relation() {
        return "m_relation";
    }

    @GetMapping("/m_success")
    public String m_success() {
        return "m_success";
    }

    @GetMapping("/m_error")
    public String m_error() {
        return "m_error";
    }

    @GetMapping("/modelling")
    public String modelling() {
        return "import_demand";
    }

    @GetMapping("/verify")
    public String verify() {
        return "veri_selfCheck";
    }

    @GetMapping("/rebuild")
    public String rebuild() {
        return "m_demand";
    }

    @GetMapping("/import_demand")
    public String import_demand() {
        return "import_demand";
    }

    @GetMapping("/m_demand")
    public String m_demand() {
        return "m_demand";
    }

    @GetMapping("/m_report")
    public String m_report() {
        return "m_report";
    }

    @GetMapping("/import_design")
    public String import_design() {
        return "import_design";
    }
    @GetMapping("/import_modelStatis")
    public String import_modelStatis() {
        return "import_modelStatis";
    }

    @GetMapping("/m_baseSet")
    public String m_baseSet() {
        return "m_baseSet";
    }

    @GetMapping("/veri_selfCheck")
    public String veri_selfCheck() {
        return "veri_selfCheck";
    }

    @GetMapping("/veri_global")
    public String veri_global() {
        return "veri_global";
    }

    @GetMapping("/veri_part")
    public String veri_part() {
        return "veri_part";
    }

    @GetMapping("/veri_reportShow")
    public String veri_reportShow(Model model) {
        Long repId = errorReportDao.findMaxRepId();
        model.addAttribute("repId", repId);
        return "veri_reportShow";
    }

    @GetMapping("/veri_reportTitle2")
    public String veri_reportTitle2() {
        return "veri_reportTitle2";
    }

    @GetMapping("/veri_repStatis")
    public String veri_repStatis() {
        return "veri_repStatis";
    }

}
