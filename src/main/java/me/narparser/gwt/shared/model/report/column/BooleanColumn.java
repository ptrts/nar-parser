package me.narparser.gwt.shared.model.report.column;

import me.narparser.gwt.shared.model.report.Column;

public class BooleanColumn extends Column<Boolean> {

    public BooleanColumn(String name, int width, HorizontalAlignment horizontalAlignment, int index) {
        super(name, width, horizontalAlignment, index);
    }

    // Для десериализации
    @SuppressWarnings("unused")
    private BooleanColumn() {
    }
}
