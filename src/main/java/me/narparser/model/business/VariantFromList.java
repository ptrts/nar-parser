package me.narparser.model.business;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VariantFromList {

    @Id
    String id;

    String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
