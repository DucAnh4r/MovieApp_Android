package com.example.movieapp.Domain.movieKind;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Params {

    @SerializedName("type_slug")
    @Expose
    private String typeSlug;
    @SerializedName("filterCategory")
    @Expose
    private List<String> filterCategory;
    @SerializedName("filterCountry")
    @Expose
    private List<String> filterCountry;
    @SerializedName("filterYear")
    @Expose
    private List<String> filterYear;
    @SerializedName("filterType")
    @Expose
    private List<String> filterType;
    @SerializedName("sortField")
    @Expose
    private String sortField;
    @SerializedName("sortType")
    @Expose
    private String sortType;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public String getTypeSlug() {
        return typeSlug;
    }

    public void setTypeSlug(String typeSlug) {
        this.typeSlug = typeSlug;
    }

    public List<String> getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(List<String> filterCategory) {
        this.filterCategory = filterCategory;
    }

    public List<String> getFilterCountry() {
        return filterCountry;
    }

    public void setFilterCountry(List<String> filterCountry) {
        this.filterCountry = filterCountry;
    }

    public List<String> getFilterYear() {
        return filterYear;
    }

    public void setFilterYear(List<String> filterYear) {
        this.filterYear = filterYear;
    }

    public List<String> getFilterType() {
        return filterType;
    }

    public void setFilterType(List<String> filterType) {
        this.filterType = filterType;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}
