package cat.ferreria.dekstop.dataaccess;

import cat.ferreria.dekstop.model.clazz.Usuario;
import cat.ferreria.dekstop.model.dtos.LibroDTO;
import cat.ferreria.dekstop.model.dtos.UsuarioDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    public boolean registrarUsuarioEnAPI(Usuario usuario) {
        String apiUrl = "http://localhost:9090/usuarios";
        Gson gson = new Gson();

        JsonObject jsonUsuario = new JsonObject();
        jsonUsuario.addProperty("dni", usuario.getDni());
        jsonUsuario.addProperty("nombre", usuario.getNombre());
        jsonUsuario.addProperty("correoElectronico", usuario.getCorreoElectronico());
        jsonUsuario.addProperty("contrasenya", usuario.getContrasena());

        String usuarioJson = jsonUsuario.toString();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = usuarioJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            return responseCode == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean modificarUsuarioEnAPI(String dniAntiguo, UsuarioDTO usuarioNuevo) {
        String apiUrl = "http://localhost:9090/usuarios/" + dniAntiguo; // DNI viejo
        Gson gson = new Gson();

        JsonObject jsonUsuario = new JsonObject();
        jsonUsuario.addProperty("dni", usuarioNuevo.getDni());  // DNI nuevo
        jsonUsuario.addProperty("nombre", usuarioNuevo.getNombre());
        jsonUsuario.addProperty("correoElectronico", usuarioNuevo.getCorreoElectronico());

        String usuarioJson = jsonUsuario.toString();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = usuarioJson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            return responseCode == 200 || responseCode == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarUsuarioEnAPI(String dni) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9090/usuarios/" + dni))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }



    public boolean validarCredencialesEnAPI(String correo, String contrasena) {
        String apiUrl = "http://localhost:9090/usuarios/sesion";

        try {
            String urlParameters = "correoElectronico=" + correo + "&contrasenya=" + contrasena;

            byte[] postData = urlParameters.getBytes("utf-8");

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData, 0, postData.length);
            }

            int responseCode = conn.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream()
            ));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            return responseCode == 200;

        } catch (Exception e) {
            e.printStackTrace();
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

    public String fetchAllLibros() {
        return fetchData("http://localhost:9090/libros").join();
    }

    public String fetchHistorial() {
        return fetchData("http://localhost:9090/historial").join();
    }

    public String fetchUsuarios() {
        String apiUrl = "http://localhost:9090/usuarios";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            conn.disconnect();
            return response.toString().trim(); // 🔥 Elimina espacios innecesarios

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String fetchLibroByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.err.println("ISBN nulo o vacío, no se puede realizar la solicitud");
            return null;
        }
        return fetchData("http://localhost:9090/libros/" + isbn).join();
    }

    public boolean eliminarLibroPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.err.println("ISBN nulo o vacío, no se puede eliminar el libro");
            return false;
        }
        try {
            URL url = new URL("http://localhost:9090/libros/" + isbn);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            int responseCode = conn.getResponseCode();
            return responseCode >= 200 && responseCode < 300;
        } catch (Exception e) {
            System.err.println("Error al eliminar el libro con ISBN " + isbn + ": " + e.getMessage());
            return false;
        }
    }
}