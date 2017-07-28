package me.narparser.gwt.client.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import me.narparser.gwt.client.ClientSideContext;
import me.narparser.gwt.client.service.GwtService;
import me.narparser.gwt.client.service.GwtServiceAsync;
import me.narparser.gwt.shared.model.report.Column;
import me.narparser.gwt.shared.model.report.ReportModel;
import me.narparser.gwt.shared.model.report.Row;

public class ReportGrid implements IsWidget {

    private static final GwtServiceAsync gwtService = GWT.create(GwtService.class);

    private Grid<Row> grid;

    private ReportGrid(ReportModel reportModel) {

        // В первой колонке лежит строка с ключем
        final ListStore<Row> store = new ListStore<>(new ModelKeyProvider<Row>() {
            @Override
            public String getKey(Row row) {
                return row.getKey();
            }
        });

        store.addAll(reportModel.getRows());

        List<ColumnConfig<Row, ?>> columnConfigs = new ArrayList<>(reportModel.getColumns().length);

        ColumnConfig<Row, String> buttonColumnConfig = null;

        for (Column<Serializable> column : reportModel.getColumns()) {

            if (!column.getName().equals("variant_id")) {

                ValueProvider<Row, Serializable> valueProvider = column.buildValueProvider();

                ColumnConfig<Row, Serializable> columnConfig = new ColumnConfig<>(valueProvider, column.getWidth(),
                        column.getName());

                if (column.getName().equals("address")) {

                    ColumnConfig<Row, ?> rawButtonColumnConfig = columnConfig;

                    //noinspection unchecked
                    buttonColumnConfig = (ColumnConfig<Row, String>) rawButtonColumnConfig;
                }

                columnConfigs.add(columnConfig);
            }
        }

        final Column<String> idColumn = reportModel.getColumn("variant_id");
        final Column<String> addressColumn = reportModel.getColumn("address");

        TextButtonCell button = new TextButtonCell();
        button.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

                int index = event.getContext().getIndex();

                Row row = grid.getStore().get(index);

                String variantId = idColumn.getValue(row);
                String address = addressColumn.getValue(row);

                VariantForm.activate(variantId, address);
            }
        });

        //noinspection ConstantConditions
        buttonColumnConfig.setCell(button);

        ColumnModel<Row> cm = new ColumnModel<>(columnConfigs);

        grid = new Grid<>(store, cm);
        grid.setAllowTextSelection(true);
        grid.getView().setStripeRows(true);
        grid.getView().setColumnLines(true);
        grid.setBorders(false);
        grid.setColumnReordering(true);

        grid.setSelectionModel(new GridSelectionModel<Row>());
        grid.getView().setForceFit(true);
        grid.getView().layout();
    }

    public static void get(String reportName, final Map<String, Serializable> param, final OnReadyCallback
            onReadyCallback) {

        gwtService.getReport(reportName, param, new AsyncCallback<ReportModel>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(ReportModel reportModel) {
                onReadyCallback.onReady(new ReportGrid(reportModel));
            }
        });
    }

    public static void activate(final String reportName, final String tabCaption, final Map<String, Serializable> param) {
        get(reportName, param, new OnReadyCallback() {
            @Override
            public void onReady(ReportGrid grid) {
                ClientSideContext.add(grid, tabCaption);
            }
        });
    }

    @Override
    public Grid<Row> asWidget() {
        return grid;
    }

    public interface OnReadyCallback {

        void onReady(ReportGrid grid);
    }
}
