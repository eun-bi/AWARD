package com.example.user.myapplication.AwardResult;

/**
 * Created by User on 2016-12-10.
 */
public class SearchData {

    String videoId;
    String title;
    String url;
    String description;

    public SearchData(String videoId, String title, String url, String description) {
        this.videoId = videoId;
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
