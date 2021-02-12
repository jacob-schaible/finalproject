package com.example.finalproject.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class User implements Comparable<User>, Parcelable {

    private int id;
    private String name;
    private String username;
    private String email;
    private Address address;
    private String phone;
    private String website;
    private Company company;
    private String avatarUrl;

    public User() {
    }

    public User(int id, String name, String username, String email, Address address,
                String phone, String website, Company company, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.company = company;
        this.avatarUrl = avatarUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public int compareTo(User u) {
        return name.equals(u.getName()) ? Integer.compare(id, u.getId()) : name.compareTo(u.getName());
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "User with ID %d named %s", id, name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeParcelable(address, 6);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeParcelable(company, 3);
        dest.writeString(avatarUrl);
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        username = in.readString();
        email = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        phone = in.readString();
        website = in.readString();
        company = in.readParcelable(Company.class.getClassLoader());
        avatarUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}