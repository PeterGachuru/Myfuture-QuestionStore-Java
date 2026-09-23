package ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;

public class UserRequestContext {
    private static ThreadLocal<String> currentUserName = new InheritableThreadLocal<>();

    public static String getCurrentUserName() {
        return currentUserName.get();
    }

    public static void setCurrentUserName(String userName) {
        currentUserName.set(userName);
    }

    public static void clear() {
        currentUserName.set(null);
    }
    private static ThreadLocal<User> currentUser = new InheritableThreadLocal<>();
    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }
    public static User getcurrentUser() {
        return currentUser.get();
    }
}
