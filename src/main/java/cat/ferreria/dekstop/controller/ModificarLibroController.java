package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.clazz.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.function.Consumer;

public class ModificarLibroController {

    @FXML TextField txtISBN;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtEditorial;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cmbEstadoUso;
    @FXML private Button btnGuardar;

    private ApiClient apiClient = new ApiClient();

    private Consumer<Libro> onLibroModificado;

    public void setOnLibroModificado(Consumer<Libro> onLibroModificado) {
        this.onLibroModificado = onLibroModificado;
    }

    @FXML
    private void initialize() {

    }
}
