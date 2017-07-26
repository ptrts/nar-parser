package me.narparser.model.business;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Photo implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Variant variant;

    @Id
    private String fileName;

    @Column(length = 500)
    private String url;

    private long length;

    private String contentType;

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
