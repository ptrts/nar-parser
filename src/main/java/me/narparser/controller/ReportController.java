package me.narparser.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("report")
public class ReportController {

    @RequestMapping("sells")
    public String sells() {
        return "sells";
    }
}
