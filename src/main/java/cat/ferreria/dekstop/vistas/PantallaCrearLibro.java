package cat.ferreria.dekstop.vistas;

import cat.ferreria.dekstop.controller.CrearLibroController;
import cat.ferreria.dekstop.model.clazz.Libro;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Map;
import java.util.function.Consumer;

public class PantallaCrearLibro {

    private Consumer<Libro> onLibroCreadoCallback;
    private Map<String, String> messages;

    public PantallaCrearLibro(Consumer<Libro> onLibroCreadoCallback, Map<String, String> messages) {
        this.onLibroCreadoCallback = onLibroCreadoCallback;
        this.messages = messages;
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/crearLibro.fxml"));
            Parent root = loader.load();

            CrearLibroController controller = loader.getController();
            if (onLibroCreadoCallback != null) {
                controller.setOnLibroCreado(onLibroCreadoCallback);
            }
            controller.setMessages(messages);

            Stage stage = new Stage();
            stage.setTitle(messages.get("app.title")); // Usar el título traducido
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}