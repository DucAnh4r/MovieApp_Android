
package com.example.movieapp.Domain.Search;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SeoOnPage {

    @SerializedName("og_type")
    @Expose
    private String ogType;
    @SerializedName("titleHead")
    @Expose
    private String titleHead;
    @SerializedName("descriptionHead")
    @Expose
    private String descriptionHead;
    @SerializedName("og_image")
    @Expose
    private List<String> ogImage;
    @SerializedName("og_url")
    @Expose
    private String ogUrl;

    public String getOgType() {
        return ogType;
    }

    public void setOgType(String ogType) {
        this.ogType = ogType;
    }

    public String getTitleHead() {
        return titleHead;
    }

    public void setTitleHead(String titleHead) {
        this.titleHead = titleHead;
    }

    public String getDescriptionHead() {
        return descriptionHead;
    }

    public void setDescriptionHead(String descriptionHead) {
        this.descriptionHead = descriptionHead;
    }

    public List<String> getOgImage() {
        return ogImage;
    }

    public void setOgImage(List<String> ogImage) {
        this.ogImage = ogImage;
    }

    public String getOgUrl() {
        return ogUrl;
    }

    public void setOgUrl(String ogUrl) {
        this.ogUrl = ogUrl;
    }

}
