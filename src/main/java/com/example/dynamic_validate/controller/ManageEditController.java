package com.example.dynamic_validate.controller;

import com.example.dynamic_validate.dao.SamlTypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class ManageEditController {
    @Autowired
    private SamlTypeDao samlTypeDao;

}
