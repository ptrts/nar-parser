package me.narparser.gwt.shared.model.report;

import java.io.Serializable;

public class Row implements Serializable {

    private static int nextId = 0;

    private String key;

    private Serializable[] data;

    // Для десериализации
    @SuppressWarnings("unused")
    private Row() {
    }

    public Row(Serializable[] data) {
        this.key = "" + (nextId++);
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    Serializable getValue(int i) {
        return data[i];
    }

    void setValue(int i, Serializable value) {
        data[i] = value;
    }
}
