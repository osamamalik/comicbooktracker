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

    /**
     * Instantiates a new Search data object.
     *
     * @param cName the comic name
     * @param sYear the start year
     * @param pub   the publisher
     * @param iURL  the image url
     * @param cID   the comic id
     */
    SearchDataObject(String cName, String sYear, String pub, String iURL, String cID){
        comicName = cName;
        startYear = sYear;
        publisher = pub;
        imageURL = iURL;
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
     * Gets start year.
     *
     * @return the start year
     */
    public String getStartYear() {
        return startYear;
    }

    /**
     * Sets start year.
     *
     * @param startYear the start year
     */
    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    /**
     * Gets publisher.
     *
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets publisher.
     *
     * @param publisher the publisher
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
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
