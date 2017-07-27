package me.narparser.controller;

import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import me.narparser.model.business.Project;
import me.narparser.service.MainService;

@Controller
@RequestMapping("project/{project}")
public class MainController {

    @Autowired
    private MainService mainService;

    @Autowired
    private HibernateTemplate hibernate;

    @RequestMapping("load/all")
    public void load(@PathVariable("project") int projectId, PrintWriter out) {
        out.println("Loading started...");
        out.flush();
        Project project = hibernate.get(Project.class, projectId);
        mainService.loadVariantsWithData(project, out);
        out.println("Complete");
    }

    @RequestMapping("load/list")
    public void loadList(@PathVariable("project") int projectId, PrintWriter out) {
        out.println("Loading started...");
        out.flush();
        Project project = hibernate.get(Project.class, projectId);
        mainService.loadList(project);
        out.println("Complete");
    }

    @RequestMapping("load/variants")
    public void loadVariants(@PathVariable("project") int projectId, PrintWriter out) {
        out.println("Loading started...");
        out.flush();
        Project project = hibernate.get(Project.class, projectId);
        mainService.loadVariants(project);
        out.println("Complete");
    }

    @RequestMapping("load/data")
    public void loadData(@PathVariable("project") int projectId, PrintWriter out) {
        out.println("Loading started...");
        out.flush();
        Project project = hibernate.get(Project.class, projectId);
        mainService.loadVariantsData(project, out);
        out.println("Complete");
    }

    @RequestMapping("image/{variant}")
    public void image(@PathVariable("project") int projectId, PrintWriter out) {
        out.println("Loading started...");
        out.flush();
        Project project = hibernate.get(Project.class, projectId);
        mainService.loadVariantsData(project, out);
        out.println("Complete");
    }

    @RequestMapping("chart")
    public String chart() {
        return "chart";
    }
}
