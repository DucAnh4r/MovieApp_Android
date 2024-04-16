
package com.example.movieapp.Domain.movieDetail;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("created")
    @Expose
    private Created created;
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
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("poster_url")
    @Expose
    private String posterUrl;
    @SerializedName("thumb_url")
    @Expose
    private String thumbUrl;
    @SerializedName("is_copyright")
    @Expose
    private Boolean isCopyright;
    @SerializedName("sub_docquyen")
    @Expose
    private Boolean subDocquyen;
    @SerializedName("chieurap")
    @Expose
    private Boolean chieurap;
    @SerializedName("trailer_url")
    @Expose
    private String trailerUrl;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("episode_current")
    @Expose
    private String episodeCurrent;
    @SerializedName("episode_total")
    @Expose
    private String episodeTotal;
    @SerializedName("quality")
    @Expose
    private String quality;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("notify")
    @Expose
    private String notify;
    @SerializedName("showtimes")
    @Expose
    private String showtimes;
    @SerializedName("year")
    @Expose
    private Integer year;
    @SerializedName("view")
    @Expose
    private Integer view;
    @SerializedName("actor")
    @Expose
    private List<String> actor;
    @SerializedName("director")
    @Expose
    private List<String> director;
    @SerializedName("category")
    @Expose
    private List<Category> category;
    @SerializedName("country")
    @Expose
    private List<Country> country;

    public Created getCreated() {
        return created;
    }

    public void setCreated(Created created) {
        this.created = created;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Boolean getIsCopyright() {
        return isCopyright;
    }

    public void setIsCopyright(Boolean isCopyright) {
        this.isCopyright = isCopyright;
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

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
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

    public String getEpisodeTotal() {
        return episodeTotal;
    }

    public void setEpisodeTotal(String episodeTotal) {
        this.episodeTotal = episodeTotal;
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

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(String showtimes) {
        this.showtimes = showtimes;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    public List<String> getActor() {
        return actor;
    }

    public void setActor(List<String> actor) {
        this.actor = actor;
    }

    public List<String> getDirector() {
        return director;
    }

    public void setDirector(List<String> director) {
        this.director = director;
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
