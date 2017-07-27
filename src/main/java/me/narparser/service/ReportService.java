package me.narparser.service;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import me.narparser.gwt.shared.model.report.Column;
import me.narparser.gwt.shared.model.report.ReportModel;
import me.narparser.gwt.shared.model.report.Row;
import me.narparser.gwt.shared.model.report.column.BooleanColumn;
import me.narparser.gwt.shared.model.report.column.IntegerColumn;
import me.narparser.gwt.shared.model.report.column.StringColumn;
import me.narparser.utils.ResourceUtils;

@Service
public class ReportService {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    public ReportModel getReport(String name, Map<String, Serializable> param) {
        String sql = ResourceUtils.getResourceAsString("report/" + name + ".sql");
        return jdbc.query(sql, param, new ResultSetExtractor<ReportModel>() {
            @Override
            public ReportModel extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                return getReportModel(resultSet);
            }
        });
    }

    private ReportModel getReportModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        int columnCount = metaData.getColumnCount();

        Column<?>[] columns = new Column<?>[columnCount];

        for (int i = 0; i < columnCount; i++) {

            String columnName = metaData.getColumnName(i + 1);

            String className = metaData.getColumnClassName(i + 1);

            Column<?> column;

            switch (className) {
                case "java.lang.Boolean":
                    column = new BooleanColumn(columnName, 200, Column.HorizontalAlignment.CENTER, i);
                    break;
                case "java.lang.Integer":
                    column = new IntegerColumn(columnName, 200, Column.HorizontalAlignment.CENTER, i);
                    break;
                case "java.lang.String":
                    column = new StringColumn(columnName, 200, Column.HorizontalAlignment.CENTER, i);
                    break;
                default:
                    column = new Column<>(columnName, 200, Column.HorizontalAlignment.CENTER, i);
            }

            columns[i] = column;
        }

        List<Row> rows = new LinkedList<>();

        while (rs.next()) {

            Serializable[] data = new Serializable[columnCount];

            for (int i = 0; i < columnCount; i++) {
                data[i] = (Serializable) rs.getObject(i + 1);
            }

            Row row = new Row(data);

            rows.add(row);
        }

        return new ReportModel(columns, rows);
    }
}
