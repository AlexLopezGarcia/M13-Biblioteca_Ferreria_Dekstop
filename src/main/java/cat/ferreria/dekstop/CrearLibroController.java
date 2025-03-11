package cat.ferreria.dekstop;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class CrearLibroController {

    @FXML private TextField txtISBN;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtEditorial;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtCantidad; // Opcional: para usarlo según necesidad
    @FXML private ComboBox<String> cmbEstadoUso;
    @FXML private Button btnGuardar;

    private ApiClient apiClient = new ApiClient();

    // Callback para notificar que se ha creado un libro
    private Consumer<Libro> onLibroCreado;

    public void setOnLibroCreado(Consumer<Libro> onLibroCreado) {
        this.onLibroCreado = onLibroCreado;
    }

    @FXML
    public void initialize() {
        cmbEstadoUso.getItems().addAll("Nuevo", "Usado", "Deteriorado");
        btnGuardar.setOnAction(event -> guardarLibro());
    }

    private void guardarLibro() {
        String isbn = this.txtISBN.getText();
        if (isbn.isEmpty()) {
            showAlert("Error", "Debes ingresar un ISBN");
            return;
        }

        // Verificar si el libro ya existe en la API
        String existingLibro = apiClient.fetchLibroByIsbn(isbn);
        if (existingLibro != null) {
            showAlert("Error", "El libro con ISBN " + isbn + " ya existe en la base de datos");
            return;
        }

        // Si no existe, proceder a guardar
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        String editorial = txtEditorial.getText();
        String categoria = txtCategoria.getText();
        String estado = cmbEstadoUso.getSelectionModel().getSelectedItem();

        if (titulo.isEmpty() || autor.isEmpty() || categoria.isEmpty() || estado == null) {
            showAlert("Error", "Completa todos los campos");
            return;
        }

        LibroDTO libroDTO = new LibroDTO(isbn, titulo, autor, editorial, categoria, estado);
        String response = apiClient.createLibro(libroDTO);

        if (response != null) {
            showAlert("Éxito", "El libro ha sido añadido correctamente");
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Error", "No se ha podido añadir el libro");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}