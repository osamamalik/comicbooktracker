package com.mohawk.osama.capstone;

/**
 * Data object containing information for a specific issue
 */
public class IssueDataObject {
    private String imageURL;
    private String issueNumber;
    private String issueName;
    private String issueDate;
    private String issueDescription;
    private String issueID;
    private int inCollection;
    private int issueRating;

    /**
     * Instantiates a new Issue data object.
     */
    IssueDataObject() {
    }

    /**
     * Instantiates a new Issue data object.
     *
     * @param iURL         the url
     * @param iNumber      the number
     * @param iName        the name
     * @param iDate        the date
     * @param iDescription the description
     * @param iID          the id
     * @param inC          the in c
     * @param iRating      the rating
     */
    IssueDataObject(String iURL, String iNumber, String iName, String iDate, String iDescription, String iID, int inC, int iRating){
        imageURL = iURL;
        issueNumber = iNumber;
        issueName = iName;
        issueDate = iDate;
        issueDescription = iDescription;
        issueID = iID;
        inCollection = inC;
        issueRating = iRating;
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
        this.issueDescription = issueID;
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
     * Gets issue rating.
     *
     * @return the issue rating
     */
    public int getIssueRating() {
        return issueRating;
    }

    /**
     * Sets issue rating.
     *
     * @param isRead the is read
     */
    public void setIssueRating(int isRead) {
        this.issueRating = issueRating;
    }
}
