package cat.ferreria.dekstop;

import cat.ferreria.dekstop.bussines.Model.LibroDTO;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class ApiClient {

    private final ExecutorService executor = Executors.newCachedThreadPool(); // Manejo eficiente de hilos

    // Obtener la lista de historial
    public String fetchHistorial() {
        return fetchData("http://localhost:9090/libros");
    }

    // Obtener los detalles de un libro por ISBN
    public String fetchLibroByIsbn(String isbn) {
        return fetchData("http://localhost:9090/libros/" + isbn);
    }

    // Método POST para crear un libro
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
                return "Error: " + responseCode + " - " + readResponse(conn.getErrorStream());
            }
            return readResponse(conn.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al conectar con la API: " + e.getMessage();
        }
    }
    // Método DELETE para eliminar un libro por ISBN
    public boolean eliminarLibroPorIsbn(String isbn) {
        String apiUrl = "http://localhost:9090/libros/" + isbn;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Método mejorado para realizar peticiones GET con FutureTask
    public String fetchData(String apiUrl) {
        Future<String> future = executor.submit(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    return "ERROR: " + responseCode + " - " + readResponse(conn.getErrorStream());
                }

                return readResponse(conn.getInputStream());

            } catch (Exception e) {
                e.printStackTrace();
                return "ERROR: " + e.getMessage();
            }
        });

        try {
            String response = future.get();
            if (response.startsWith("ERROR: ")) {
                System.err.println("API ERROR: " + response);
                return null;
            }
            return response;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para leer la respuesta del InputStream
    private String readResponse(InputStream inputStream) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }
}