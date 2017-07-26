package me.narparser.gwt.shared.model.report;

import com.sencha.gxt.core.client.ValueProvider;

import java.io.Serializable;

public class Column<T extends Serializable> implements Serializable {

    /**
     * Для сериализации
     */
    @SuppressWarnings("unused")
    protected Column() {
    }

    public enum HorizontalAlignment {
        LEFT, RIGHT, CENTER
    }

    protected String name;
    protected int index;
    protected int width;
    protected HorizontalAlignment horizontalAlignment;

    public Object render(T t) {
        return t.toString();
    }

    public Column(String name, int width, HorizontalAlignment horizontalAlignment, int index) {
        this.name = name;
        this.width = width;
        this.horizontalAlignment = horizontalAlignment;
        this.index = index;
    }

    public ValueProvider<Row, T> buildValueProvider() {

        return new ValueProvider<Row, T>() {

            @SuppressWarnings("unchecked")
            @Override
            public T getValue(Row row) {
                return Column.this.getValue(row);
            }

            @Override
            public void setValue(Row row, T value) {
                getaVoid(row, value);
            }

            @Override
            public String getPath() {
                return name;
            }
        };
    }

    public void getaVoid(Row row, T value) {
        row.setValue(index, value);
    }

    @SuppressWarnings("unchecked")
    public T getValue(Row row) {
        return (T) row.getValue(index);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }
}
