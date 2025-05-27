package cat.ferreria.dekstop.vistas;

import cat.ferreria.dekstop.controller.ModificarLibroController;
import cat.ferreria.dekstop.model.clazz.Libro;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public class PantallaModificarLibro {

    private final Consumer<Libro> onLibroModificado;
    private final Libro libroSeleccionado;
    private final Map<String, String> messages;

    public PantallaModificarLibro(Consumer<Libro> onLibroModificado, Libro libroSeleccionado, Map<String, String> messages) {
        this.onLibroModificado = onLibroModificado;
        this.libroSeleccionado = libroSeleccionado;
        this.messages = messages;
    }

    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modificarLibro.fxml"));
        Scene scene = new Scene(loader.load());
        ModificarLibroController controller = loader.getController();
        controller.setOnLibroModificado(onLibroModificado);
        controller.setLibroSeleccionado(libroSeleccionado);
        controller.setMessages(messages);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(messages.get("app.title"));
        stage.show();
    }
}