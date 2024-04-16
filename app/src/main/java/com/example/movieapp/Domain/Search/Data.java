
package com.example.movieapp.Domain.Search;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("seoOnPage")
    @Expose
    private SeoOnPage seoOnPage;
    @SerializedName("breadCrumb")
    @Expose
    private List<BreadCrumb> breadCrumb;
    @SerializedName("titlePage")
    @Expose
    private String titlePage;
    @SerializedName("items")
    @Expose
    private List<Item> items;
    @SerializedName("params")
    @Expose
    private Params params;
    @SerializedName("type_list")
    @Expose
    private String typeList;
    @SerializedName("APP_DOMAIN_FRONTEND")
    @Expose
    private String appDomainFrontend;
    @SerializedName("APP_DOMAIN_CDN_IMAGE")
    @Expose
    private String appDomainCdnImage;

    public SeoOnPage getSeoOnPage() {
        return seoOnPage;
    }

    public void setSeoOnPage(SeoOnPage seoOnPage) {
        this.seoOnPage = seoOnPage;
    }

    public List<BreadCrumb> getBreadCrumb() {
        return breadCrumb;
    }

    public void setBreadCrumb(List<BreadCrumb> breadCrumb) {
        this.breadCrumb = breadCrumb;
    }

    public String getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(String titlePage) {
        this.titlePage = titlePage;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getTypeList() {
        return typeList;
    }

    public void setTypeList(String typeList) {
        this.typeList = typeList;
    }

    public String getAppDomainFrontend() {
        return appDomainFrontend;
    }

    public void setAppDomainFrontend(String appDomainFrontend) {
        this.appDomainFrontend = appDomainFrontend;
    }

    public String getAppDomainCdnImage() {
        return appDomainCdnImage;
    }

    public void setAppDomainCdnImage(String appDomainCdnImage) {
        this.appDomainCdnImage = appDomainCdnImage;
    }

}
