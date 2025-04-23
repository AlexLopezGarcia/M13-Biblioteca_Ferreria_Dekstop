package cat.ferreria.dekstop.vistas;

import cat.ferreria.dekstop.controller.ModificarLibroController;
import cat.ferreria.dekstop.model.clazz.Libro;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class PantallaModificarLibro {

    Consumer<Libro> onLibroModificadoCallback;

    public PantallaModificarLibro(Consumer<Libro> onLibroModificadoCallback) {
        this.onLibroModificadoCallback = onLibroModificadoCallback;
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modificarLibro.fxml"));
            Parent root = loader.load();

            ModificarLibroController controller = loader.getController();
            if (onLibroModificadoCallback != null) {
                controller.setOnLibroModificado(onLibroModificadoCallback);
            }

            Stage stage = new Stage();
            stage.setTitle("Modificar Libro");//constante
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());

        }
    }
}
