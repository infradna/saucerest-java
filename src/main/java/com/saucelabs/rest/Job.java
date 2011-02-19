package com.saucelabs.rest;

public class Job extends UpdateJob {
    public String id;
    public String owner;
    public String error;
    public String browser;
    public String browserVersion;
    public String os;
    public int creation_time;
    public int start_time;
    public int end_time;
    public String video_url;
    public String log_url;

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", error='" + error + '\'' +
                ", browser='" + browser + '\'' +
                ", browserVersion='" + browserVersion + '\'' +
                ", os='" + os + '\'' +
                ", creation_time=" + creation_time +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", video_url='" + video_url + '\'' +
                ", log_url='" + log_url + '\'' +
                "} " + super.toString();
    }
}
