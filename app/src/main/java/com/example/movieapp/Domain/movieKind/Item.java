
package com.example.movieapp.Domain.movieKind;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("modified")
    @Expose
    private Modified modified;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("origin_name")
    @Expose
    private String originName;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("poster_url")
    @Expose
    private String posterUrl;
    @SerializedName("thumb_url")
    @Expose
    private String thumbUrl;
    @SerializedName("sub_docquyen")
    @Expose
    private Boolean subDocquyen;
    @SerializedName("chieurap")
    @Expose
    private Boolean chieurap;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("episode_current")
    @Expose
    private String episodeCurrent;
    @SerializedName("quality")
    @Expose
    private String quality;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("year")
    @Expose
    private Integer year;
    @SerializedName("category")
    @Expose
    private List<Category> category;
    @SerializedName("country")
    @Expose
    private List<Country> country;

    public Modified getModified() {
        return modified;
    }

    public void setModified(Modified modified) {
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public Boolean getSubDocquyen() {
        return subDocquyen;
    }

    public void setSubDocquyen(Boolean subDocquyen) {
        this.subDocquyen = subDocquyen;
    }

    public Boolean getChieurap() {
        return chieurap;
    }

    public void setChieurap(Boolean chieurap) {
        this.chieurap = chieurap;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEpisodeCurrent() {
        return episodeCurrent;
    }

    public void setEpisodeCurrent(String episodeCurrent) {
        this.episodeCurrent = episodeCurrent;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public List<Country> getCountry() {
        return country;
    }

    public void setCountry(List<Country> country) {
        this.country = country;
    }

}
