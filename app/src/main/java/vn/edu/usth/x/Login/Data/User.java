package vn.edu.usth.x.Login.Data;

public class User {
    private String name;
    private String email;
    private String dob;
    private String passwordHash;
    private String profileImageBase64;
    private String displayName;

    public User() {
        name = "";
        email = "";
        dob = "";
        passwordHash = "";
        profileImageBase64 = "";
        displayName = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImageBase64 = profileImageBase64;
    }

    public String getProfileImageBase64() {
        return profileImageBase64;
    }
}