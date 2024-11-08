// File: UserManager.java
package vn.edu.usth.x.Utils;

import android.content.Context;

import vn.edu.usth.x.Login.Data.User;

public class UserManager {
    private static User user;
    private static UserManager instance;
    private final Context context;
    private static String currentUsername;
    private static String currentID;
    private static String DisplayName;
    private static boolean isVerified;
    private static Integer following;
    private static Integer followers;



    private UserManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public static void followerIncrement() {
        followers++;
    }

    public static void followerDecrement() {
        followers--;
    }

    public static String getCurrentID() {
        return currentID;
    }

    public static void setCurrentID(String currentID) {
        UserManager.currentID = currentID;
    }

    public static String getDisplayName() {
        return DisplayName;
    }

    public static void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public static boolean isIsVerified() {
        return isVerified;
    }

    public static void setIsVerified(boolean isVerified) {
        UserManager.isVerified = isVerified;
    }

    public static Integer getFollowing() {
        return following;
    }

    public static void setFollowing(Integer following) {
        UserManager.following = following;
    }

    public static Integer getFollowers() {
        return followers;
    }

    public static void setFollowers(Integer followers) {
        UserManager.followers = followers;
    }

    public static void setUser(User newUser) {
        user = newUser;
    }
    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }
    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }
}