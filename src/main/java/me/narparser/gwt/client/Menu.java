package me.narparser.gwt.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import me.narparser.gwt.client.forms.ReportGrid;
import me.narparser.gwt.client.forms.VariantForm;
import me.narparser.gwt.client.service.GwtService;
import me.narparser.gwt.client.service.GwtServiceAsync;
import me.narparser.gwt.shared.model.ProjectBean;

public class Menu implements IsWidget {

    private static final GwtServiceAsync gwtService = GWT.create(GwtService.class);
    
    private VerticalLayoutContainer widget = null;

    private Margins buttonMargins = new Margins(2, 2, 0, 2);

    private VerticalLayoutContainer.VerticalLayoutData buttonLayoutData = new VerticalLayoutContainer
            .VerticalLayoutData(
            200, 30, buttonMargins);

    @Override
    public Widget asWidget() {

        if (widget == null) {

            widget = new VerticalLayoutContainer();

            //widget.setScrollSupport();
            widget.setScrollMode(ScrollSupport.ScrollMode.AUTOY);
            //widget.setAdjustForScroll();

            addReports();
        }

        return widget;
    }

    private void addItem(String text, SelectEvent.SelectHandler handler) {
        TextButton button = new TextButton(text);
        button.setWidth(200);
        button.addSelectHandler(handler);
        widget.add(button, buttonLayoutData);
    }

    private void addReport(final String name) {
        addReport(name, name, null);
    }

    private void addReport(final String reportName, final String text, final Map<String, Serializable> param) {
        addItem(text, new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                ReportGrid.activate(reportName, text, param);
            }
        });
    }

    private void addReports() {

        gwtService.getProjects(new AsyncCallback<List<ProjectBean>>() {
            
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<ProjectBean> projects) {

                for (ProjectBean project : projects) {
                    addProjectReports(project);
                }

                addReport("sells");
                addReport("priceDown");
                addReport("new");
                
                addItem("Наша квартира", new SelectEvent.SelectHandler() {
                    @Override
                    public void onSelect(SelectEvent event) {
                        VariantForm.activate("580672", "Наша квартира");
                    }
                });
            }
        });
    }

    private void addProjectReports(ProjectBean project) {
        
        Map<String, Serializable> params = new HashMap<>();
        params.put("project_id", project.getId());

        String projectCaption = "" + project.getName();
        
        addReport("variants", projectCaption + ": варианты", params);
        addReport("openVariants", projectCaption + ": открытые", params);
        addReport("sellsByProject", projectCaption + ": продажи", params);
        addReport("priceDownByProject", projectCaption + ": снижения", params);
        addReport("newByProject", projectCaption + ": новые", params);
    }
}
