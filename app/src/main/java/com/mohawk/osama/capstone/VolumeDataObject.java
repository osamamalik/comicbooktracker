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
    private int comicRating;
    private int comicID;
    private String comicName;

    /**
     * Instantiates a new Volume data object.
     */
    VolumeDataObject() {
    }

    /**
     * Instantiates a new Volume data object.
     *
     * @param cURL         the comic cover url
     * @param cYear        the comic start year
     * @param cPublisher   the comic publisher
     * @param cCount       the comic issue count
     * @param cDescription the comic description
     * @param inC          the in collection status
     * @param cRating      the comic rating
     * @param cID          the comic id
     * @param cName        the comic name
     */
    VolumeDataObject(String cURL, String cYear, String cPublisher, String cCount, String cDescription, int inC, int cRating, int cID, String cName){
        imageURL = cURL;
        comicYear = cYear;
        comicPublisher = cPublisher;
        comicCount = cCount;
        comicDescription = cDescription;
        inCollection = inC;
        comicRating = cRating;
        comicID = cID;
        comicName = cName;
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
     * Gets comic year.
     *
     * @return the comic year
     */
    public String getComicYear() {
        return comicYear;
    }

    /**
     * Sets comic year.
     *
     * @param comicYear the comic year
     */
    public void setComicYear(String comicYear) {
        this.comicYear = comicYear;
    }

    /**
     * Gets comic publisher.
     *
     * @return the comic publisher
     */
    public String getComicPublisher() {
        return comicPublisher;
    }

    /**
     * Sets comic publisher.
     *
     * @param comicPublisher the comic publisher
     */
    public void setComicPublisher(String comicPublisher) {
        this.comicPublisher = comicPublisher;
    }

    /**
     * Gets comic count.
     *
     * @return the comic count
     */
    public String getComicCount() {
        return comicCount;
    }

    /**
     * Sets comic count.
     *
     * @param comicCount the comic count
     */
    public void setComicCount(String comicCount) {
        this.imageURL = comicCount;
    }

    /**
     * Gets comic description.
     *
     * @return the comic description
     */
    public String getComicDescription() {
        return comicDescription;
    }

    /**
     * Sets comic description.
     *
     * @param comicDescription the comic description
     */
    public void setComicDescription(String comicDescription) {
        this.comicDescription = comicDescription;
    }

    /**
     * Gets in collection.
     *
     * @return the in collection
     */
    public int getInCollection() {
        return inCollection;
    }

    /**
     * Sets in collection.
     *
     * @param inCollection the in collection
     */
    public void setInCollection(int inCollection) {
        this.inCollection = inCollection;
    }

    /**
     * Gets comic rating.
     *
     * @return the comic rating
     */
    public int getComicRating() {
        return comicRating;
    }

    /**
     * Sets comic rating.
     *
     * @param inCollection the in collection
     */
    public void setComicRating(int inCollection) {
        this.comicRating = comicRating;
    }

    /**
     * Gets comic id.
     *
     * @return the comic id
     */
    public int getComicID() {
        return comicID;
    }

    /**
     * Sets comic id.
     *
     * @param comicID the comic id
     */
    public void setComicID(int comicID) {
        this.comicID = comicID;
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
}
