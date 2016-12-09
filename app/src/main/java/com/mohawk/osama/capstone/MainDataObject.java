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


    /**
     * Instantiates a new Main data object.
     *
     * @param cName the comic name
     * @param iName the issue name
     * @param rDate the read date
     * @param iURL  the issue cover url
     * @param iID   the issue id
     * @param cID   the comic id
     */
    MainDataObject(String cName, String iName, String rDate, String iURL, String iID, String cID){
        comicName = cName;
        issueName = iName;
        releaseDate = rDate;
        imageURL = iURL;
        issueID = iID;
        comicID = cID;
    }

    /**
     * Gets comic name.
     *
     * @return the comic name
     */
    public String getComicName() {
        return comicName;
    }

    /**
     * Sets comic name.
     *
     * @param comicName the comic name
     */
    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    /**
     * Gets issue name.
     *
     * @return the issue name
     */
    public String getIssueName() {
        return issueName;
    }

    /**
     * Sets issue name.
     *
     * @param issueName the issue name
     */
    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }

    /**
     * Gets release date.
     *
     * @return the release date
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets release date.
     *
     * @param releaseDate the release date
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Gets image url.
     *
     * @return the image url
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Sets image url.
     *
     * @param imageURL the image url
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * Gets issue id.
     *
     * @return the issue id
     */
    public String getIssueID() {
        return issueID;
    }

    /**
     * Sets issue id.
     *
     * @param issueID the issue id
     */
    public void setIssueID(String issueID) {
        this.issueID = issueID;
    }

    /**
     * Gets comic id.
     *
     * @return the comic id
     */
    public String getComicID() {
        return comicID;
    }

    /**
     * Sets comic id.
     *
     * @param comicID the comic id
     */
    public void setComicID(String comicID) {
        this.comicID = comicID;
    }
}
