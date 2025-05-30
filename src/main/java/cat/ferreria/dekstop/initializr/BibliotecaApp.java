package cat.ferreria.dekstop.initializr;

import cat.ferreria.dekstop.controller.BibliotecaController;
import cat.ferreria.dekstop.dataaccess.ApiClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Lanzador de la aplicacion Biblioteca.
 *
 * @author alexl
 * @date 10/02/2025
 * */

public class BibliotecaApp extends Application {

    private ApiClient apiClient = new ApiClient();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<String, String> messages = null;
        for (int i = 0; i < 3 && messages == null; i++) {
            messages = apiClient.fetchTranslations("es");
            if (messages == null) Thread.sleep(1000);
        }
        if (messages == null) {
            System.err.println("No se pudieron cargar las traducciones iniciales.");
            messages = Map.of(
                    "app.title", "Biblioteca",
                    "alert.error", "Error",
                    "alert.no.seleccionado", "No se ha seleccionado ningún libro",
                    "alert.error.conexion", "Error de conexión con el servidor",
                    "alert.exito", "Éxito",
                    "alert.libro.anyadido", "El libro ha sido añadido correctamente",
                    "alert.libro.noanyadido", "No se ha podido añadir el libro",
                    "alert.libro.eliminado", "Libro eliminado correctamente",
                    "alert.libro.noeliminado", "No se ha podido eliminar el libro"
            );
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/biblioteca.fxml"));
        Parent root = loader.load();

        BibliotecaController controller = loader.getController();
        controller.setMessages(messages);
        controller.setMessageFetcher(apiClient::fetchTranslations);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle(messages.get("app.title"));
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}