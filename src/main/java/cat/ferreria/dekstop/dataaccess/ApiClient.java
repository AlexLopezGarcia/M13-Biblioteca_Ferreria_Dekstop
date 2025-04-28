package cat.ferreria.dekstop.dataaccess;

import cat.ferreria.dekstop.AuthContext;
import cat.ferreria.dekstop.model.dtos.LibroDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:9090";

    public String autenticar(String usuario, String contrasenya) {
        try {
            URL urlLogin = new URL(BASE_URL + "/auth/login");
            HttpURLConnection conn = (HttpURLConnection) urlLogin.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String loginInput = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", usuario, contrasenya);
            System.out.println("Enviando login: " + loginInput);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(loginInput.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Código de respuesta de login: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = readResponse(conn);
                System.out.println("Respuesta de login: " + response);
                String token = new Gson().fromJson(response, TokenResponse.class).getToken();
                System.out.println("Token recibido: " + token);
                AuthContext.setJwtToken(token);
                return token;
            } else {
                System.err.println("Login fallido: " + responseCode + " - " + readResponse(conn));
                return "";
            }
        } catch (Exception e) {
            System.err.println("Error al hacer el login: " + e.getMessage());
            return "";
        }
    }

    public Map<String, String> fetchTranslations(String language) {
        try {
            URL url = new URL(BASE_URL + "/bibliotecaferreria/i18n/messages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Language", language);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = readResponse(conn);
                return new Gson().fromJson(response, new TypeToken<Map<String, String>>(){}.getType());
            } else {
                System.err.println("Error al obtener traducciones: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error al obtener traducciones: " + e.getMessage());
            return null;
        }
    }

    private CompletableFuture<String> fetchData(String endpoint) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                System.out.println("Enviando solicitud a: " + endpoint);
                int responseCode = conn.getResponseCode();
                System.out.println("Código de respuesta: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return readResponse(conn);
                } else {
                    System.err.println("Error en la solicitud a " + endpoint + ": " + responseCode);
                    return null;
                }
            } catch (Exception e) {
                System.err.println("Error al realizar la solicitud a " + endpoint + ": " + e.getMessage());
                return null;
            }
        });
    }

    public String createLibro(LibroDTO libroDTO) {
        String apiUrl = "/public/libros";
        try {
            URL url = new URL(BASE_URL + apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonInput = new Gson().toJson(libroDTO);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_CREATED && responseCode != HttpURLConnection.HTTP_OK) {
                return "Error: " + responseCode + " - " + readResponse(conn);
            }
            return readResponse(conn);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al conectar con la API: " + e.getMessage();
        }
    }

    public String fetchAllLibros() {
        return fetchData("/public/libros").join();
    }

    public String fetchHistorial() {
        return fetchData("/private/historial").join();
    }

    public String fetchLibroByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.err.println("ISBN nulo o vacío, no se puede realizar la solicitud");
            return null;
        }
        return fetchData("/public/libros/" + isbn).join();
    }

    public boolean eliminarLibroPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.err.println("ISBN nulo o vacío, no se puede eliminar el libro");
            return false;
        }
        try {
            URL url = new URL(BASE_URL + "/public/libros/" + isbn);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            return responseCode >= 200 && responseCode < 300;
        } catch (Exception e) {
            System.err.println("Error al eliminar el libro con ISBN " + isbn + ": " + e.getMessage());
            return false;
        }
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                StringBuilder response = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    response.append((char) c);
                }
                return response.toString();
            }
        } else {
            try (InputStreamReader reader = new InputStreamReader(conn.getErrorStream())) {
                StringBuilder errorResponse = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    errorResponse.append((char) c);
                }
                throw new Exception("Error en la solicitud HTTP: " + responseCode + " - " + errorResponse.toString());
            }
        }
    }

    private static class TokenResponse {
        private String token;

        public String getToken() {
            return token;
        }
    }
}