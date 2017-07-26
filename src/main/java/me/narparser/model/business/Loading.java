package me.narparser.model.business;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Loading implements Serializable {

    @Id
    @GeneratedValue
    int id;

    @ManyToOne(fetch = FetchType.LAZY)
    Project project;

    Date loadingDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Date getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(Date loadingDate) {
        this.loadingDate = loadingDate;
    }
}
