package com.example.athulk.hajarpattika;

public class UpdateInfo {
    private double mVersion;
    private String mNews;

    public UpdateInfo(double version, String news){
        mVersion = version;
        mNews = news;
    }

    public double getmVersion(){return mVersion;}
    public String getmNews(){return mNews;}
}
