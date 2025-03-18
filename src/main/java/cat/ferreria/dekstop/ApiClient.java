package cat.ferreria.dekstop;

import cat.ferreria.dekstop.bussines.Model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    // Obtener la lista de historial de libros
    public String fetchHistorial() {
        String apiUrl = "http://localhost:9090/libros";
        return fetchData(apiUrl);
    }

    // Obtener los detalles de un libro por ISBN
    public String fetchLibroByIsbn(String isbn) {
        String apiUrl = "http://localhost:9090/libros/" + isbn;
        return fetchData(apiUrl);
    }

    // Obtener la lista de usuarios
    public String fetchUsuarios() {
        String apiUrl = "http://localhost:9090/usuarios";
        return fetchData(apiUrl);
    }

    public boolean registrarUsuarioEnAPI(Usuario usuario) {
        String apiUrl = "http://localhost:9090/usuarios";
        Gson gson = new Gson();

        // Crear manualmente el JSON con "contrasenya" en lugar de "contrasena"
        JsonObject jsonUsuario = new JsonObject();
        jsonUsuario.addProperty("dni", usuario.getDni());
        jsonUsuario.addProperty("nombre", usuario.getNombre());
        jsonUsuario.addProperty("correoElectronico", usuario.getCorreoElectronico());
        jsonUsuario.addProperty("contrasenya", usuario.getContrasena()); // 🔧 Corrección

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
            return responseCode == 201; // Código 201 = Creado correctamente
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String fetchData(String apiUrl) {
        try {
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

