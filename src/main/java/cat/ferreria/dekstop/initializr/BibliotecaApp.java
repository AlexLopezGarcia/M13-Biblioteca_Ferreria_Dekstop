package cat.ferreria.dekstop.initializr;

import cat.ferreria.dekstop.controller.BibliotecaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BibliotecaApp extends Application {

    private static final String API_URL = "http://localhost:9090/bibliotecaferreria/i18n/messages";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private Map<String, String> messages;

    @Override
    public void start(Stage primaryStage) throws Exception {
        messages = fetchMessages("es");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/biblioteca.fxml"));
        Parent root = loader.load();

        BibliotecaController controller = loader.getController();
        controller.setMessages(messages);
        controller.setMessageFetcher(this::fetchMessages);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle(messages.get("app.title"));
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }

    private Map<String, String> fetchMessages(String lang) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Accept-Language", lang)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), Map.class);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar mensajes desde la API: " + e.getMessage());
        }

        try {
            Locale locale = new Locale(lang);
            ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", locale);
            Map<String, String> messages = new HashMap<>();
            for (String key : bundle.keySet()) {
                messages.put(key, bundle.getString(key));
            }
            return messages;
        } catch (Exception e) {
            System.err.println("Error al cargar mensajes locales: " + e.getMessage());
            return Map.ofEntries(
                    Map.entry("libro.isbn", "ISBN"),
                    Map.entry("libro.titulo", "Título"),
                    Map.entry("libro.autor", "Autor"),
                    Map.entry("libro.categoria", "Categoría"),
                    Map.entry("libro.estado", "Estado"),
                    Map.entry("app.title", "Biblioteca"),
                    Map.entry("button.registrar.devolucion", "Registrar Devolución"),
                    Map.entry("button.registrar.prestamo", "Registrar Préstamo"),
                    Map.entry("button.eliminar.libro", "Eliminar Libro"),
                    Map.entry("button.modificar.libro", "Modificar Libro"),
                    Map.entry("button.anyadir.libro", "Añadir Libro"),
                    Map.entry("button.logarse", "Logarse"),
                    Map.entry("button.registrar.usuario", "Registrar Usuario"),
                    Map.entry("button.buscar", "Buscar"),
                    Map.entry("button.recargar.lista", "Recargar Lista"),
                    Map.entry("alert.error", "Error"),
                    Map.entry("alert.no.seleccionado", "No se ha seleccionado ningún libro"),
                    Map.entry("alert.exito", "Éxito"),
                    Map.entry("alert.completa.campos", "Completa todos los campos"),
                    Map.entry("alert.cantidad.invalida", "Debes ingresar una cantidad válida"),
                    Map.entry("alert.isbn.existe", "El libro con ISBN {0} ya existe en la base de datos"),
                    Map.entry("alert.cantidad.rango", "La cantidad debe estar entre 0 y 99"),
                    Map.entry("alert.cantidad.numero", "La cantidad debe ser un número entero"),
                    Map.entry("label.registro", "Formulario de Registro"),
                    Map.entry("label.dni", "DNI"),
                    Map.entry("label.nombre", "Nombre"),
                    Map.entry("label.correo", "Correo Electrónico"),
                    Map.entry("label.contrasena", "Contraseña"),
                    Map.entry("label.registrar.usuario", "Registro Usuario")
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}