package me.narparser.model.business;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class VariantStatusChange implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    Variant variant;

    @Id
    @Temporal(TemporalType.TIMESTAMP)
    Date loadingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    Loading loading;

    boolean open;

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public Date getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(Date changeDate) {
        this.loadingDate = changeDate;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Loading getLoading() {
        return loading;
    }

    public void setLoading(Loading loading) {
        this.loading = loading;
    }
}
