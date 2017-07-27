package me.narparser.gwt.shared.model.report;

import java.io.Serializable;
import java.util.List;

public class ReportModel implements Serializable {

    // Чтоб в массив можно было класть колонки с разным содержимым, мы снимаем ограничение на тип содержимого
    // Содержимое будет извлекаться в виде Serializable
    // Снятие ограничения на содержимое в общем случае подвергает контейнеры опасности, что в них положат что-то
    // им не свойственное. Но мы обещаем только читать, и ничего не класть в коллекции
    private Column<?>[] columns;

    private List<Row> rows;

    @SuppressWarnings("unused")
    private ReportModel() {
    }

    public ReportModel(Column<?>[] columns, List<Row> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    @SuppressWarnings("unchecked")
    public Column<Serializable>[] getColumns() {
        // Мы не боимся кастить колонки что в них лежит Serializable, потому что
        // берем на себя обязательство ничего не класть в эти колонки
        //
        // Отделение модели от вида подразумевает предоставление наружу своих потрохов
        //
        // Дело в том, что на посмотреть отдавать наружу не страшно, а вот на изменить - страшновато
        //
        // Но, это можно преодолеть
        return (Column<Serializable>[]) columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public <T extends Serializable> Column<T> getColumn(String name) {
        for (Column<?> column : columns) {
            if (column.getName().equals(name)) {
                //noinspection unchecked
                return (Column<T>) column;
            }
        }
        return null;
    }
}
