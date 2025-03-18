package cat.ferreria.dekstop;

import cat.ferreria.dekstop.Controller.ModificarLibroController;
import cat.ferreria.dekstop.bussines.Model.Libro;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class PantallaModificarLibro {
    private Libro libro;
    private Consumer<Libro> onLibroModificadoCallback;

    public PantallaModificarLibro(Libro libro, Consumer<Libro> onLibroModificadoCallback) {
        this.libro = libro;
        this.onLibroModificadoCallback = onLibroModificadoCallback;
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ModificarLibro.fxml"));
            Parent root = loader.load();

            ModificarLibroController controller = loader.getController();
            controller.setLibro(libro);

            if (onLibroModificadoCallback != null) {
                controller.setOnLibroModificado(onLibroModificadoCallback);
            }

            Stage stage = new Stage();
            stage.setTitle("Modificar Libro");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar el archivo FXML");
        }
    }
}
