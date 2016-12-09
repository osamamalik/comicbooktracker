package com.mohawk.osama.capstone;

/**
 * Created by Osama on 11/16/2016.
 */
public class NewThisMonthDataObject {
    private String imageURL;
    private String issueNumber;
    private String issueName;
    private String issueDate;
    private String issueDescription;
    private String issueID;
    private String comicName;

    /**
     * Instantiates a new New this month data object.
     */
    NewThisMonthDataObject() {
    }

    /**
     * Instantiates a new New this month data object.
     *
     * @param iURL         the image url
     * @param iNumber      the issue number
     * @param iName        the issue name
     * @param iDate        the release date
     * @param iDescription the issue description
     * @param iID          the issue id
     * @param cName        the comic name
     */
    NewThisMonthDataObject(String iURL, String iNumber, String iName, String iDate, String iDescription, String iID, String cName){
        imageURL = iURL;
        issueNumber = iNumber;
        issueName = iName;
        issueDate = iDate;
        issueDescription = iDescription;
        issueID = iID;
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
     * Gets issue number.
     *
     * @return the issue number
     */
    public String getIssueNumber() {
        return issueNumber;
    }

    /**
     * Sets issue number.
     *
     * @param issueNumber the issue number
     */
    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
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
     * Gets issue date.
     *
     * @return the issue date
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * Sets issue date.
     *
     * @param issueDate the issue date
     */
    public void setIssueDate(String issueDate) {
        this.imageURL = issueDate;
    }

    /**
     * Gets issue description.
     *
     * @return the issue description
     */
    public String getIssueDescription() {
        return issueDescription;
    }

    /**
     * Sets issue description.
     *
     * @param issueDescription the issue description
     */
    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
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
