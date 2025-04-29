package cat.ferreria.dekstop.initializr;

import cat.ferreria.dekstop.controller.BibliotecaController;
import cat.ferreria.dekstop.dataaccess.ApiClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Map;

public class BibliotecaApp extends Application {

    private ApiClient apiClient = new ApiClient();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<String, String> messages = apiClient.fetchTranslations("es");
        if (messages == null) {
            System.err.println("No se pudieron cargar las traducciones iniciales.");
            messages = Map.of("app.title", "Biblioteca"); // Fallback
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