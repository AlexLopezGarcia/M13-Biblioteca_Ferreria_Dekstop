package cat.ferreria.dekstop.Controller;

import cat.ferreria.dekstop.ApiClient;
import cat.ferreria.dekstop.bussines.Model.Libro;
import cat.ferreria.dekstop.bussines.Model.LibroDTO;
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

        txtCantidad.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCantidad.setText(oldValue);
            } else {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value < 0 || value > 99) {
                        txtCantidad.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    txtCantidad.setText(oldValue);
                }
            }
        });
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

        if (txtCantidad.getText().isEmpty()) {
            showAlert("Error", "Debes ingresar una cantidad valida"); //constante
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad < 0 || cantidad > 99) {
                showAlert("Error", "La cantidad debe estar entre 0 y 99"); //constante
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "La cantidad debe ser un numero entero"); //constante
            return;
        }

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