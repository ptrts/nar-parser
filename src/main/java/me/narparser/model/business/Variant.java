package me.narparser.model.business;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Variant implements Serializable {

    @Id
    private String id;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variant")
    @MapKey(name = "fileName")
    private Map<String, Photo> photos;

    private Date lastLoadingDate;

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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Date getLastLoadingDate() {
        return lastLoadingDate;
    }

    public void setLastLoadingDate(Date lastLoadingDate) {
        this.lastLoadingDate = lastLoadingDate;
    }

    public Map<String, Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Map<String, Photo> photos) {
        this.photos = photos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variant variant = (Variant) o;

        return !(id != null ? !id.equals(variant.id) : variant.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
