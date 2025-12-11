package com.mangomusic.model;

import java.time.LocalDate;

public class User {

    private Integer userId;  // Changed from int
    private String username;
    private String email;
    private LocalDate signupDate;
    private String subscriptionType;
    private String country;

    public User() {
    }

    public User(Integer userId, String username, String email, LocalDate signupDate,
                String subscriptionType, String country) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.signupDate = signupDate;
        this.subscriptionType = subscriptionType;
        this.country = country;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(LocalDate signupDate) {
        this.signupDate = signupDate;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", signupDate=" + signupDate +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}