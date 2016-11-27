package com.mohawk.osama.capstone;

/**
 * Created by Osama on 11/14/2016.
 */
public class IssuesDataObject {
    private String imageURL;
    private String issueNumber;
    private String issueName;
    private String issueDate;
    private String issueDescription;
    private String issueID;
    private String comicName;
    private String comicID;
    private int isRead;

    IssuesDataObject() {
    }

    IssuesDataObject(String iURL, String iNumber, String iName, String iDate, String iDescription, String iID, String cName, String cID, int iR){
        imageURL = iURL;
        issueNumber = iNumber;
        issueName = iName;
        issueDate = iDate;
        issueDescription = iDescription;
        issueID = iID;
        comicName = cName;
        comicID = cID;
        isRead = iR;
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
        this.issueID = issueID;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getComicID() {
        return comicID;
    }

    public void setComicID(String comicID) {
        this.comicID = comicID;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
