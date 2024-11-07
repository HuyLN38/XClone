// File: UserManager.java
package vn.edu.usth.x.Utils;

import vn.edu.usth.x.Login.Data.User;

public class UserManager {
    private static User user;

    private UserManager() {

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
}