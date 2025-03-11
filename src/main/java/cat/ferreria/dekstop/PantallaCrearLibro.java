package cat.ferreria.dekstop;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class PantallaCrearLibro {

    private Consumer<Libro> onLibroCreadoCallback;

    public PantallaCrearLibro(Consumer<Libro> onLibroCreadoCallback) {
        this.onLibroCreadoCallback = onLibroCreadoCallback;
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CrearLibro.fxml"));
            Parent root = loader.load();

            CrearLibroController controller = loader.getController();
            if (onLibroCreadoCallback != null) {
                controller.setOnLibroCreado(onLibroCreadoCallback);
            }

            Stage stage = new Stage();
            stage.setTitle("Crear Libro");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar el archivo FXML");
        }
    }
}