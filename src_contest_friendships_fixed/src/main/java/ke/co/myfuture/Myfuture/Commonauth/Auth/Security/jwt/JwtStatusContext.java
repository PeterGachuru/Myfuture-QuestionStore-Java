package ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt;

public class JwtStatusContext {
    private static ThreadLocal<Boolean> expiredJWT = new InheritableThreadLocal<>();

    public static Boolean getExpiredJWT() {
        return expiredJWT.get();
    }

    public static void setExpiredJWT(Boolean userName) {
        expiredJWT.set(userName);
    }
}
