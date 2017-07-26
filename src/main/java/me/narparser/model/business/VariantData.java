package me.narparser.model.business;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class VariantData implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    Variant variant;

    @Id
    @Temporal(TemporalType.TIMESTAMP)
    Date loadingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    Loading loading;

    Date changeDate;

    Date postDate;

    int views;

    String shortCode;

    int price;

    int rooms;

    String city;

    String district;

    String street;

    String building;

    String type;

    int floor;

    int floors;

    String material;

    @Column(precision = 10, scale = 2)
    float area;

    @Column(precision = 10, scale = 2)
    float livingArea;

    @Column(precision = 10, scale = 2)
    float kitchenArea;

    String layout;

    String balcony;

    String bathroom;

    String phone;

    boolean pureSell;

    boolean exchange;

    String property;

    @Column(length = 5000)
    String description;

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public float getLivingArea() {
        return livingArea;
    }

    public void setLivingArea(float livingArea) {
        this.livingArea = livingArea;
    }

    public float getKitchenArea() {
        return kitchenArea;
    }

    public void setKitchenArea(float kitchenArea) {
        this.kitchenArea = kitchenArea;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getBalcony() {
        return balcony;
    }

    public void setBalcony(String balcony) {
        this.balcony = balcony;
    }

    public String getBathroom() {
        return bathroom;
    }

    public void setBathroom(String bathroom) {
        this.bathroom = bathroom;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPureSell() {
        return pureSell;
    }

    public void setPureSell(boolean pureSell) {
        this.pureSell = pureSell;
    }

    public boolean isExchange() {
        return exchange;
    }

    public void setExchange(boolean exchange) {
        this.exchange = exchange;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
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

    public boolean sameData(VariantData that) {

        if (that == null) {
            return false;
        }

        if (views != that.views) return false;
        if (price != that.price) return false;
        if (rooms != that.rooms) return false;
        if (floor != that.floor) return false;
        if (floors != that.floors) return false;
        if (Float.compare(that.area, area) != 0) return false;
        if (Float.compare(that.livingArea, livingArea) != 0) return false;
        if (Float.compare(that.kitchenArea, kitchenArea) != 0) return false;
        if (pureSell != that.pureSell) return false;
        if (exchange != that.exchange) return false;
        if (!changeDate.equals(that.changeDate)) return false;
        if (!postDate.equals(that.postDate)) return false;
        if (!shortCode.equals(that.shortCode)) return false;
        if (!city.equals(that.city)) return false;
        if (!district.equals(that.district)) return false;
        if (!street.equals(that.street)) return false;
        if (!building.equals(that.building)) return false;
        if (!type.equals(that.type)) return false;
        if (!material.equals(that.material)) return false;
        if (!layout.equals(that.layout)) return false;
        if (!balcony.equals(that.balcony)) return false;
        if (!bathroom.equals(that.bathroom)) return false;
        if (!phone.equals(that.phone)) return false;
        if (!property.equals(that.property)) return false;
        return description.equals(that.description);
    }
}
