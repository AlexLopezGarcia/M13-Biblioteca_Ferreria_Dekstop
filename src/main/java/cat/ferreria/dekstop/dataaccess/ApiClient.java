package cat.ferreria.dekstop.dataaccess;

import cat.ferreria.dekstop.model.dtos.LibroDTO;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class ApiClient {

    private CompletableFuture<String> fetchData(String endpoint) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                return readResponse(conn);
            } catch (Exception e) {
                System.err.println("Error al realizar la solicitud a " + endpoint + ": " + e.getMessage());
                return null;
            }
        });
    }

    public String createLibro(LibroDTO libroDTO) {
        String apiUrl = "http://localhost:9090/libros";
        try {
            URL url = new URL(apiUrl);
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

    public String fetchAllLibros() {
        return fetchData("http://localhost:9090/libros").join();
    }

    public String fetchHistorial() {
        return fetchData("http://localhost:9090/historial").join();
    }

    public String fetchLibroByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.err.println("ISBN nulo o vacío, no se puede realizar la solicitud");
            return null;
        }
        return fetchData("http://localhost:9090/libros/" + isbn).join();
    }

    public boolean eliminarLibroPorId(long libro_id) {
        if (libro_id == 0) {
            System.err.println("ID nulo o vacío, no se puede eliminar el libro");
            return false;
        }
        try {
            System.out.println("Intentando eliminar libro con ID: " + libro_id);
            URL url = new URL("http://localhost:9090/libros/" + libro_id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("Libro con ID " + libro_id + " eliminado correctamente");
                return true;
            } else {
                System.err.println("Error al eliminar el libro con ID " + libro_id + ": Código de respuesta " + responseCode);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar el libro con ID " + libro_id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}