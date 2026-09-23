package ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.HttpInterceptor;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Users.Users;

public class UserDetailsRequestContext {

    private static ThreadLocal<String> currentUserDetails = new InheritableThreadLocal<>();
    private static ThreadLocal<Users> currentUser = new InheritableThreadLocal<>();
    public static void setCurrentUser(Users user) {
        currentUser.set(user);
    }
    public static Users getcurrentUser() {
        return currentUser.get();
    }
    public static String getcurrentUserDetails() {
        return currentUserDetails.get();
    }

    public static void setCurrentUserDetails(String userDetails) {
        currentUserDetails.set(userDetails);
    }


    public static void clear() {
        currentUserDetails.set(null);
    }


}