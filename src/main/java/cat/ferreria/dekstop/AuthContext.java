package cat.ferreria.dekstop;

public class AuthContext {
    private static String currentJwtToken;

    public static void setJwtToken(String token) {
        currentJwtToken = token;
    }

    public static String getJwtToken() {
        return currentJwtToken;
    }

    public static void clear() {
        currentJwtToken = null;
    }

}
