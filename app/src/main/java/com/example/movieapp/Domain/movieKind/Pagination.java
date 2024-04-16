
package com.example.movieapp.Domain.movieKind;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("totalItems")
    @Expose
    private Integer totalItems;
    @SerializedName("totalItemsPerPage")
    @Expose
    private Integer totalItemsPerPage;
    @SerializedName("currentPage")
    @Expose
    private Integer currentPage;
    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getTotalItemsPerPage() {
        return totalItemsPerPage;
    }

    public void setTotalItemsPerPage(Integer totalItemsPerPage) {
        this.totalItemsPerPage = totalItemsPerPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

}
