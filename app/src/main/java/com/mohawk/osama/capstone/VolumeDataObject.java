package com.mohawk.osama.capstone;

/**
 * Created by Osama on 11/15/2016.
 */
public class VolumeDataObject {
    private String imageURL;
    private String comicYear;
    private String comicPublisher;
    private String comicCount;
    private String comicDescription;
    private int inCollection;

    VolumeDataObject() {
    }

    VolumeDataObject(String cURL, String cYear, String cPublisher, String cCount, String cDescription, int inC){
        imageURL = cURL;
        comicYear = cYear;
        comicPublisher = cPublisher;
        comicCount = cCount;
        comicDescription = cDescription;
        inCollection = inC;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getComicYear() {
        return comicYear;
    }

    public void setComicYear(String comicYear) {
        this.comicYear = comicYear;
    }

    public String getComicPublisher() {
        return comicPublisher;
    }

    public void setComicPublisher(String comicPublisher) {
        this.comicPublisher = comicPublisher;
    }

    public String getComicCount() {
        return comicCount;
    }

    public void setComicCount(String comicCount) {
        this.imageURL = comicCount;
    }

    public String getComicDescription() {
        return comicDescription;
    }

    public void setComicDescription(String comicDescription) {
        this.comicDescription = comicDescription;
    }

    public int getInCollection() {
        return inCollection;
    }

    public void setInCollection(int inCollection) {
        this.inCollection = inCollection;
    }
}
