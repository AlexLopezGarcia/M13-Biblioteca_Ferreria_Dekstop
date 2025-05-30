package cat.ferreria.dekstop;

/**
 * AuthContext se utiliza para almacenar el token JWT actual de la aplicación.
 *
 * @author alexl
 * @date 15/10/2024
 * */

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
