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
            try (OutputStream os = conn.getOutputStream()) {
                os.write(loginInput.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = readResponse(conn);
                String token = new Gson().fromJson(response, TokenResponse.class).getToken();
                AuthContext.setJwtToken(token);
                return token;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public Map<String, String> fetchTranslations(String language) {
        try {
            URL url = new URL(BASE_URL + "/public/bibliotecaferreria/i18n/messages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Language", language);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = readResponse(conn);
                return new Gson().fromJson(response, new TypeToken<Map<String, String>>(){}.getType());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private CompletableFuture<String> fetchData(String endpoint) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (AuthContext.getJwtToken() != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + AuthContext.getJwtToken());
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return readResponse(conn);
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        });
    }

    public LibroDTO createLibro(LibroDTO libroDTO) throws Exception {
        String apiUrl = "/public/libros";
        try {
            URL url = new URL(BASE_URL + apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            if (AuthContext.getJwtToken() != null) {
                conn.setRequestProperty("Authorization", "Bearer " + AuthContext.getJwtToken());
            }

            String jsonInput = new Gson().toJson(libroDTO);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                return new Gson().fromJson(readResponse(conn), LibroDTO.class);
            }
            throw new Exception("Error: " + responseCode + " - " + readResponse(conn));
        } catch (Exception e) {
            throw new Exception("Error al conectar con la API: " + e.getMessage());
        }
    }

    public LibroDTO updateLibro(LibroDTO libroDTO) throws Exception {
        String apiUrl = "/public/libros/" + libroDTO.getIsbn();
        try {
            URL url = new URL(BASE_URL + apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            if (AuthContext.getJwtToken() != null) {
                conn.setRequestProperty("Authorization", "Bearer " + AuthContext.getJwtToken());
            }

            String jsonInput = new Gson().toJson(libroDTO);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return new Gson().fromJson(readResponse(conn), LibroDTO.class);
            }
            throw new Exception("Error: " + responseCode + " - " + readResponse(conn));
        } catch (Exception e) {
            throw new Exception("Error al conectar con la API: " + e.getMessage());
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
            return null;
        }
        return fetchData("/public/libros/" + isbn).join();
    }

    public void eliminarLibroPorIsbn(String isbn) throws Exception {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new Exception("ISBN inválido");
        }
        try {
            URL url = new URL(BASE_URL + "/public/libros/" + isbn);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            if (AuthContext.getJwtToken() != null) {
                conn.setRequestProperty("Authorization", "Bearer " + AuthContext.getJwtToken());
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new Exception("Libro no encontrado");
            } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
                throw new Exception("El libro está asociado a registros de historial");
            } else if (responseCode >= 200 && responseCode < 300) {
                return;
            }
            throw new Exception("Error al eliminar: " + responseCode);
        } catch (Exception e) {
            throw new Exception("Error al eliminar el libro: " + e.getMessage());
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