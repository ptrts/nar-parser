package me.narparser.gwt.client.forms;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import me.narparser.gwt.client.propertyaccess.VariantBeanProperties;
import me.narparser.gwt.client.service.GwtService;
import me.narparser.gwt.client.service.GwtServiceAsync;
import me.narparser.gwt.shared.model.VariantBean;

public class VariantsGrid implements IsWidget {

    private static final VariantBeanProperties props = GWT.create(VariantBeanProperties.class);

    private static final GwtServiceAsync gwtService = GWT.create(GwtService.class);

    private Widget widget;

    private int projectId;

    public VariantsGrid(int projectId) {
        this.projectId = projectId;
    }

    @Override
    public Widget asWidget() {
        if (widget == null) {

            //@formatter:off
            ColumnConfig<VariantBean, String>  codeCol     = new ColumnConfig<>(props.code()     , 140, "Code");
            ColumnConfig<VariantBean, String>  districtCol = new ColumnConfig<>(props.district() , 140, "District");
            ColumnConfig<VariantBean, String>  streetCol   = new ColumnConfig<>(props.street()   , 180, "Street");
            ColumnConfig<VariantBean, String>  buildingCol = new ColumnConfig<>(props.building() , 90, "Building");
            ColumnConfig<VariantBean, Integer> floorCol    = new ColumnConfig<>(props.floor()    , 70, "Floor");
            ColumnConfig<VariantBean, String>  typeCol     = new ColumnConfig<>(props.type()     , 180, "Type");
            ColumnConfig<VariantBean, Integer> priceCol    = new ColumnConfig<>(props.price()    , 70, "Price");
            ColumnConfig<VariantBean, Boolean> openCol     = new ColumnConfig<>(props.open()     , 70, "Open");
            //@formatter:on

            List<ColumnConfig<VariantBean, ?>> columns = new ArrayList<>();
            columns.add(codeCol);
            columns.add(districtCol);
            columns.add(streetCol);
            columns.add(buildingCol);
            columns.add(floorCol);
            columns.add(typeCol);
            columns.add(priceCol);
            columns.add(openCol);

            ColumnModel<VariantBean> cm = new ColumnModel<>(columns);

            final ListStore<VariantBean> store = new ListStore<>(props.id());

            gwtService.getVariantBeans(projectId, new AsyncCallback<List<VariantBean>>() {
                @Override
                public void onFailure(Throwable caught) {
                    Window.alert("fail");
                }

                @Override
                public void onSuccess(List<VariantBean> variantBeans) {
                    store.addAll(variantBeans);
                }
            });

            final Grid<VariantBean> grid = new Grid<>(store, cm);
            grid.setAllowTextSelection(true);
            grid.getView().setStripeRows(true);
            grid.getView().setColumnLines(true);
            grid.setBorders(false);
            grid.setColumnReordering(true);

            grid.setSelectionModel(new GridSelectionModel<VariantBean>());

            widget = grid;
        }

        return widget;
    }
}
