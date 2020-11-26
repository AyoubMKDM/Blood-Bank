package com.AyoubMKDM.github.bloodbank;

public class UserDataModel {
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
    private String userName;
    private String userCity;
    private String userBloodType;
    private String userPhoneNumber;
    private String userCountry;

    public String getUserDonationTimes() {
        return userDonationTimes;
    }

    public void setUserDonationTimes(String userDonationTimes) {
        this.userDonationTimes = userDonationTimes;
    }

    //TODO this is temporary for to approximate search using fuzzy algorithm later change it to the number of donation
    private String userDonationTimes;

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public UserDataModel() {}

    public UserDataModel(String userId, String userName, String userCity, String userBloodType,
                         String userPhoneNumber, String userCountry) {
        this.userId = userId;
        this.userName = userName;
        this.userCity = userCity;
        this.userBloodType = userBloodType;
        this.userPhoneNumber = userPhoneNumber;
        this.userCountry = userCountry;
    }


    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserBloodType() {
        return userBloodType;
    }

    public void setUserBloodType(String userBloodType) {
        this.userBloodType = userBloodType;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
