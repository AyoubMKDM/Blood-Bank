package com.AyoubMKDM.github.bloodbank;

public class DataModelPost {
    private String type;
    private String postTitle;
    private String postTextBody;
    public DataModelPost(String type, String postTitle, String postTextBody) {
        this.type = type;
        this.postTitle = postTitle;
        this.postTextBody = postTextBody;
    }
    public DataModelPost(String postTitle, String textBody) {
        this.type = "Request";
        this.postTitle = postTitle;
        this.postTextBody = textBody;
    }

    public String getType() {
        return type;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostTextBody() {
        return postTextBody;
    }

    public void setPostTextBody(String postTextBody) {
        this.postTextBody = postTextBody;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public void setType(String type) {
        this.type = type;
    }
}
