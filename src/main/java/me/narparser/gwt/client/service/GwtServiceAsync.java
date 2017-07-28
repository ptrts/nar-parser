package me.narparser.gwt.client.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import me.narparser.gwt.shared.model.ProjectBean;
import me.narparser.gwt.shared.model.VariantBean;
import me.narparser.gwt.shared.model.report.ReportModel;
import me.narparser.model.business.Project;

public interface GwtServiceAsync {

    void getVariantBeans(int projectId, AsyncCallback<List<VariantBean>> async);

    void getReport(String name, Map<String, Serializable> param, AsyncCallback<ReportModel> async);

    void getVariant(String id, AsyncCallback<VariantBean> async);

    void getProjects(AsyncCallback<List<ProjectBean>> async);
}
