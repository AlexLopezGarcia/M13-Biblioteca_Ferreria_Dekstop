package cat.ferreria.dekstop;

import cat.ferreria.dekstop.bussines.Model.Libro;
import cat.ferreria.dekstop.bussines.Model.LibroDTO;
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
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cmbEstadoUso;
    @FXML private Button btnGuardar;


    private ApiClient apiClient = new ApiClient();

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
            showAlert("Error", "Debes ingresar un ISBN"); //constante
            return;
        }

        String existLibro = apiClient.fetchLibroByIsbn(isbn);
        if (existLibro != null) {
            showAlert("Error", "El libro con ISBN " + isbn + " ya existe en la base de datos"); //constante
            return;
        }

        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        String editorial = txtEditorial.getText();
        String categoria = txtCategoria.getText();
        String estado = cmbEstadoUso.getSelectionModel().getSelectedItem();

        if (titulo.isEmpty() || autor.isEmpty() || categoria.isEmpty() || estado == null) {
            showAlert("Error", "Completa todos los campos"); //constante
            return;
        }

        LibroDTO libroDTO = new LibroDTO(isbn, titulo, autor, categoria, estado);
        String response = apiClient.createLibro(libroDTO);

        if (response != null) {
            showAlert("Exito", "El libro ha sido añadido correctamente"); //constante
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Error", "No se ha podido añadir el libro"); //constante
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