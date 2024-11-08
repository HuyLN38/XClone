// File: UserManager.java
package vn.edu.usth.x.Utils;

import android.content.Context;

import vn.edu.usth.x.Login.Data.User;

public class UserManager {
    private static User user;
    private static UserManager instance;
    private final Context context;
    private static String currentUsername;


    private UserManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
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

    public void setCurrentUsername(String username) {
        currentUsername = username;
    }
}