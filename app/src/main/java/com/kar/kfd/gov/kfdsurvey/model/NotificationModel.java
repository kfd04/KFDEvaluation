package com.kar.kfd.gov.kfdsurvey.model;

/**
 * Created by sarath on 14-08-2019.
 */
public class NotificationModel {
    private int id;
    private int status;
    private String title;
    private String message;
    private String url;
    private String timeStamp;


    public NotificationModel(int id, int status, String title, String message, String url, String timeStamp) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.message = message;
        this.url = url;
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
