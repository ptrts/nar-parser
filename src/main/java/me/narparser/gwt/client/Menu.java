package me.narparser.gwt.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import me.narparser.gwt.client.forms.ReportGrid;
import me.narparser.gwt.client.forms.VariantForm;

public class Menu implements IsWidget {

    private VerticalLayoutContainer widget = null;

    private Margins buttonMargins = new Margins(2, 2, 0, 2);

    private VerticalLayoutContainer.VerticalLayoutData buttonLayoutData = new VerticalLayoutContainer
            .VerticalLayoutData(
            150, 30, buttonMargins);

    @Override
    public Widget asWidget() {

        if (widget == null) {

            widget = new VerticalLayoutContainer();

            addReports();

            addItem("Наша квартира", new SelectEvent.SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    VariantForm.activate("580672", "Наша квартира");
                }
            });
        }

        return widget;
    }

    private void addItem(String text, SelectEvent.SelectHandler handler) {
        TextButton button = new TextButton(text);
        button.setWidth(150);
        button.addSelectHandler(handler);
        widget.add(button, buttonLayoutData);
    }

    private void addReport(final String name) {
        addReport(name, name, null);
    }

    private void addReport(final String reportName, String text, final Map<String, Serializable> param) {
        addItem(text, new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                ReportGrid.activate(reportName, param);
            }
        });
    }

    private void addReports() {

        Map<String, Serializable> param1 = new HashMap<>();
        param1.put("project_id", 1);

        addReport("variants", "variants 1", param1);
        addReport("openVariants", "openVariants 1", param1);

        Map<String, Serializable> param2 = new HashMap<>();
        param2.put("project_id", 2);

        addReport("variants", "variants 2", param2);
        addReport("openVariants", "openVariants 2", param2);

        addReport("sells");
        addReport("priceDown");
        addReport("new");
    }
}
