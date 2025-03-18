package cat.ferreria.dekstop.Controller;

import cat.ferreria.dekstop.bussines.Model.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class ModificarLibroController {
    @FXML private TextField isbnField;
    @FXML private TextField tituloField;
    @FXML private TextField autorField;
    @FXML private TextField categoriaField;
    @FXML private TextField estadoField;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Libro libro;
    private Consumer<Libro> onLibroModificadoCallback;

    public void setLibro(Libro libro) {
        this.libro = libro;
        if (libro != null) {
            isbnField.setText(libro.isbnProperty().get());
            tituloField.setText(libro.tituloProperty().get());
            autorField.setText(libro.autorProperty().get());
            categoriaField.setText(libro.categoriaProperty().get());
            estadoField.setText(libro.estadoProperty().get());
        }
    }

    public void setOnLibroModificado(Consumer<Libro> callback) {
        this.onLibroModificadoCallback = callback;
    }

    @FXML
    private void guardarCambios() {
        if (libro != null) {
            libro.setTitulo(tituloField.getText());
            libro.setAutor(autorField.getText());
            libro.setCategoria(categoriaField.getText());
            libro.setEstado(estadoField.getText());

            if (onLibroModificadoCallback != null) {
                onLibroModificadoCallback.accept(libro);
            }
            cerrarVentana();
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
