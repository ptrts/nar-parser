package me.narparser.gwt.shared.model.report;

import java.io.Serializable;

import com.sencha.gxt.core.client.ValueProvider;

public class Column<T extends Serializable> implements Serializable {

    protected String name;

    protected int index;

    protected int width;

    protected HorizontalAlignment horizontalAlignment;

    /**
     * Для сериализации
     */
    @SuppressWarnings("unused")
    protected Column() {
    }

    public Column(String name, int width, HorizontalAlignment horizontalAlignment, int index) {
        this.name = name;
        this.width = width;
        this.horizontalAlignment = horizontalAlignment;
        this.index = index;
    }

    public Object render(T t) {
        return t.toString();
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

    public enum HorizontalAlignment {
        LEFT, RIGHT, CENTER
    }
}
