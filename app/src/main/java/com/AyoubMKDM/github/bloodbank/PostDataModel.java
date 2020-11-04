package com.AyoubMKDM.github.bloodbank;

import android.os.Parcel;
import android.os.Parcelable;

public class PostDataModel implements Parcelable {
    private String userName;
    private String userId;
    private String userLocation;
    private String postId;
    private String postRequestedBloodType;
    private String postTitle;
    private String postTextBody;
    private String postDateAdding;

    public PostDataModel() {}

    public PostDataModel(String userName, String userLocation, String postRequestedBloodType,
                         String postTitle, String postTextBody, String postDateAdding) {
        this.userName = userName;
        this.userLocation = userLocation;
        this.postRequestedBloodType = postRequestedBloodType;
        this.postTitle = postTitle;
        this.postTextBody = postTextBody;
        this.postDateAdding = postDateAdding;
    }

    public PostDataModel(String postTitle, String textBody) {
        this.postRequestedBloodType = "Request";
        this.postTitle = postTitle;
        this.postTextBody = textBody;
    }

    protected PostDataModel(Parcel in) {
        userName = in.readString();
        userId = in.readString();
        userLocation = in.readString();
        postId = in.readString();
        postRequestedBloodType = in.readString();
        postTitle = in.readString();
        postTextBody = in.readString();
        postDateAdding = in.readString();
    }

    public static final Creator<PostDataModel> CREATOR = new Creator<PostDataModel>() {
        @Override
        public PostDataModel createFromParcel(Parcel in) {
            return new PostDataModel(in);
        }

        @Override
        public PostDataModel[] newArray(int size) {
            return new PostDataModel[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserLocation() {
        return userLocation;
    }


    public String getPostId() {
        return postId;
    }

    public String getPostRequestedBloodType() {
        return postRequestedBloodType;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostTextBody() {
        return postTextBody;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostDateAdding() {
        return postDateAdding;
    }

    public void setPostTextBody(String postTextBody) {
        this.postTextBody = postTextBody;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public void setPostRequestedBloodType(String postRequestedBloodType) {
        this.postRequestedBloodType = postRequestedBloodType;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setPostDateAdding(String postDateAdding) {
        this.postDateAdding = postDateAdding;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(userLocation);
        dest.writeString(postId);
        dest.writeString(postRequestedBloodType);
        dest.writeString(postTitle);
        dest.writeString(postTextBody);
        dest.writeString(postDateAdding);
    }
}
