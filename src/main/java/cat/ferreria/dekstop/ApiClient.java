package cat.ferreria.dekstop;

import cat.ferreria.dekstop.bussines.Model.LibroDTO;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    // Obtener la lista de historial
    public String fetchHistorial() {
        String apiUrl = "http://localhost:9090/libros";
        return fetchData(apiUrl);
    }

    // Obtener los detalles de un libro por ISBN
    public String fetchLibroByIsbn(String isbn) {
        String apiUrl = "http://localhost:9090/libros/" + isbn;
        return fetchData(apiUrl);
    }

    public String createLibro(LibroDTO libroDTO) {
        String apiUrl = "http://localhost:9090/libros";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            Gson gson = new Gson();
            String jsonInput = gson.toJson(libroDTO);

            System.out.println("Creando libro: " + jsonInput);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Codigo de respuesta: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_CREATED && responseCode != HttpURLConnection.HTTP_OK) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                System.err.println("Error en la API: " + errorResponse);
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            System.out.println("Respuesta de la API: " + response);
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private String fetchData(String apiUrl) {
        try {
            System.out.println("Llamando a la API con URL: " + apiUrl);

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Error HTTP: " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}