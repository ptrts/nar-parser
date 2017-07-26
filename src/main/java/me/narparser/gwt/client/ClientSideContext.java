package me.narparser.gwt.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

public class ClientSideContext {
    private static TabPanel tabPanel;

    public static void add(IsWidget widget, String text) {
        TabItemConfig config = new TabItemConfig(text, true);
        tabPanel.add(widget, config);
        tabPanel.setActiveWidget(widget);
        tabPanel.forceLayout();
    }

    public static void setTabPanel(TabPanel tabPanel) {
        ClientSideContext.tabPanel = tabPanel;
    }
}
