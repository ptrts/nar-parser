package me.narparser.gwt.client.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import me.narparser.gwt.shared.model.ProjectBean;
import me.narparser.gwt.shared.model.VariantBean;
import me.narparser.gwt.shared.model.report.ReportModel;
import me.narparser.model.business.Project;

@RemoteServiceRelativePath("rpc/gwtService")
public interface GwtService extends RemoteService {

    List<VariantBean> getVariantBeans(int projectId);

    ReportModel getReport(String name, Map<String, Serializable> param);

    VariantBean getVariant(String id);

    List<ProjectBean> getProjects();
}
