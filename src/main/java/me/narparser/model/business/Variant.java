package me.narparser.model.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Variant implements Serializable {

    @Id
    private String id;

    private String code;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Project> projects;

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

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> project) {
        this.projects = project;
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
