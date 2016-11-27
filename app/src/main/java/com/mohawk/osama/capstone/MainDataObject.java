package com.mohawk.osama.capstone;

/**
 * Created by Osama on 9/17/2016.
 */
public class MainDataObject {
    private String comicName;
    private String issueName;
    private String releaseDate;
    private String imageURL;
    private String issueID;
    private String comicID;


    MainDataObject(String cName, String iName, String rDate, String iURL, String iID, String cID){
        comicName = cName;
        issueName = iName;
        releaseDate = rDate;
        imageURL = iURL;
        issueID = iID;
        comicID = cID;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getIssueName() {
        return issueName;
    }

    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getIssueID() {
        return issueID;
    }

    public void setIssueID(String issueID) {
        this.issueID = issueID;
    }

    public String getComicID() {
        return comicID;
    }

    public void setComicID(String comicID) {
        this.comicID = comicID;
    }
}
