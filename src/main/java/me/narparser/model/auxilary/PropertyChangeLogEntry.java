package me.narparser.model.auxilary;

import me.narparser.model.business.Loading;
import me.narparser.model.business.Variant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "PropertyChangeLogEntries")
public class PropertyChangeLogEntry implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    Variant variant;

    @Id
    private String property;

    @Id
    @Temporal(TemporalType.TIMESTAMP)
    Date loadingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    Loading loading;

    @Column(length = 2000)
    private String oldValue;

    @Column(length = 2000)
    private String newValue;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "" + property + ": " + oldValue + " -> " + newValue;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public Date getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(Date loadingDate) {
        this.loadingDate = loadingDate;
    }

    public Loading getLoading() {
        return loading;
    }

    public void setLoading(Loading loading) {
        this.loading = loading;
    }
}
