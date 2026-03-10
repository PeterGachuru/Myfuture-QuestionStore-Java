package ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt;


import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.Session.Activesession;

public class CurrentUserContext {
    private static ThreadLocal<Activesession> currentActiveUser = new InheritableThreadLocal<>();

    public static Activesession getCurrentActiveUser() {
        return currentActiveUser.get();
    }


    public static void setCurrentActiveUser(Activesession entity) {
        currentActiveUser.set(entity);
    }

    public static void clear() {
        currentActiveUser.set(null);
    }
}