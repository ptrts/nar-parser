package me.narparser.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;

public class MainEntryPoint implements IsWidget, EntryPoint {

    private SimpleContainer simpleContainer;
    private ContentPanel west;
    private ContentPanel center;

    public Widget asWidget() {

        if (simpleContainer == null) {

            // BorderLayout контейнер
            final BorderLayoutContainer con = new BorderLayoutContainer();

            // Пустая панель. Запад
            west = new ContentPanel();

            // Не пустая панель. Центр
            center = new ContentPanel();
            center.setHeaderVisible(false);
            // Это чтоб таблица расплывалась на всю ContentPanel
            center.setResize(true);

            TabPanel tabPanel = new TabPanel();

            center.add(tabPanel);

            ClientSideContext.setTabPanel(tabPanel);

            BorderLayoutData westData = new BorderLayoutData(150);
            // Кругленькая картиночка в заголовке для схлопывания панельки в направлении границы
            westData.setCollapsible(true);
            // Это чтоб можно было ресайзить мышкой. setCollapseMini(true) тоже имеет такой побочный эффект
            westData.setSplit(true);
            // Длинная тонкая кнопочка на границе для схлопывания
            westData.setCollapseMini(true);
            // Границы позволяют отделить ContentPanel от соседей. Иначе она будет сливаться с ними
            westData.setMargins(new Margins(0, 8, 0, 0));

            con.setWestWidget(west, westData);
            con.setCenterWidget(center);

            simpleContainer = con;

            setContent();
        }

        return simpleContainer;
    }

    private void setContent() {
        west.add(new Menu());
    }

    public void onModuleLoad() {

        // Это вариант SimpleContainer, который расплывается на весь экран, и детей своих расплывает тоже (в чем самый и смысл)
        Viewport viewport = new Viewport();
        viewport.add(asWidget());

        // SimpleContainer, кстати, может иметь только одного потомка. Он может управлять размерами потомка, его видимостью
        // и чего-нибудь дорисовать вокруг него. Как все контейнеры, собственно

        RootPanel.get().add(viewport);
    }
}
