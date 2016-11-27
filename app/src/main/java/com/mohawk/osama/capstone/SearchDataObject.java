package com.mohawk.osama.capstone;

/**
 * Created by Osama on 9/17/2016.
 */
public class SearchDataObject {
    private String comicName;
    private String startYear;
    private String publisher;
    private String imageURL;
    private String comicID;

    SearchDataObject(String cName, String sYear, String pub, String iURL, String cID){
        comicName = cName;
        startYear = sYear;
        publisher = pub;
        imageURL = iURL;
        comicID = cID;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getComicID() {
        return comicID;
    }

    public void setComicID(String comicID) {
        this.comicID = comicID;
    }
}
