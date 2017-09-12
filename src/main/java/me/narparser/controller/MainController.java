package me.narparser.controller;

import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import me.narparser.service.MainService;

@Controller
public class MainController {

    @Autowired
    private MainService mainService;

    @RequestMapping("load/init")
    public void init(PrintWriter out) {
        mainService.createLoading();
        out.println("Complete");
    }

    @RequestMapping("load/list")
    public void loadList(PrintWriter out) {
        out.println("Loading started...");
        out.flush();
        mainService.loadList(out);
        out.println("Complete");
    }

    @RequestMapping("load/variants")
    public void loadVariants(PrintWriter out) {
        out.println("Loading started...");
        out.flush();
        mainService.loadVariants();
        out.println("Complete");
    }

    @RequestMapping("load/data")
    public void loadData(PrintWriter out) {
        out.println("Loading started...");
        out.flush();
        mainService.loadVariantsData(out);
        out.println("Complete");
    }

    @RequestMapping("load/all")
    public void load(PrintWriter out) {
        out.println("Loading started...");
        out.flush();
        mainService.createLoading();
        mainService.loadList(out);
        mainService.loadVariantsWithData(out);
        out.println("Complete");
    }
}
