package me.narparser.gwt.client.forms;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

public class TabSystem extends TabPanel {

    public TabSystem() {

        SelectionHandler<Widget> handler = new SelectionHandler<Widget>() {
            @Override
            public void onSelection(SelectionEvent<Widget> event) {

                // Смотрим, чего нажали
                Widget w = event.getSelectedItem();

                // Для того, что нажали, достаем конфиг
                TabItemConfig config = getConfig(w);
            }
        };

    }
}
