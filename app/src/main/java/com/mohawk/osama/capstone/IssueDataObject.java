package com.mohawk.osama.capstone;

/**
 * Created by Osama on 11/14/2016.
 */
public class IssueDataObject {
    private String imageURL;
    private String issueNumber;
    private String issueName;
    private String issueDate;
    private String issueDescription;
    private String issueID;
    private int inCollection;

    IssueDataObject() {
    }

    IssueDataObject(String iURL, String iNumber, String iName, String iDate, String iDescription, String iID, int inC){
        imageURL = iURL;
        issueNumber = iNumber;
        issueName = iName;
        issueDate = iDate;
        issueDescription = iDescription;
        issueID = iID;
        inCollection = inC;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getIssueName() {
        return issueName;
    }

    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.imageURL = issueDate;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getIssueID() {
        return issueID;
    }

    public void setIssueID(String issueID) {
        this.issueDescription = issueID;
    }

    public int getInCollection() {
        return inCollection;
    }

    public void setInCollection(int inCollection) {
        this.inCollection = inCollection;
    }
}
