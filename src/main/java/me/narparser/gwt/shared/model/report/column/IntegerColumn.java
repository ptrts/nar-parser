package me.narparser.gwt.shared.model.report.column;

import me.narparser.gwt.shared.model.report.Column;

public class IntegerColumn extends Column<Integer> {

    public IntegerColumn(String name, int width, HorizontalAlignment horizontalAlignment, int index) {
        super(name, width, horizontalAlignment, index);
    }

    // Для десериализации
    @SuppressWarnings("unused")
    private IntegerColumn() {
    }
}
