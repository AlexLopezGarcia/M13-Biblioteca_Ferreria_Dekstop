package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.clazz.Libro;
import cat.ferreria.dekstop.model.dtos.LibroDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.Map;
import java.util.function.Consumer;

public class CrearLibroController {

    @FXML private TextField txtISBN;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cmbEstadoUso;
    @FXML private Button btnGuardar;
    @FXML private Label isbnLabel;
    @FXML private Label tituloLabel;
    @FXML private Label autorLabel;
    @FXML private Label categoriaLabel;
    @FXML private Label cantidadLabel;
    @FXML private Label estadoLabel;

    private ApiClient apiClient = new ApiClient();
    private Consumer<Libro> onLibroCreado;
    private Map<String, String> messages;

    public void setOnLibroCreado(Consumer<Libro> onLibroCreado) {
        this.onLibroCreado = onLibroCreado;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
        updateUI();
    }

    @FXML
    public void initialize() {
        cmbEstadoUso.getItems().addAll("Disponible", "Prestado");
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

    private void updateUI() {
        if (messages == null) return;
        btnGuardar.setText(messages.get("button.anyadir.libro"));
        isbnLabel.setText(messages.get("libro.isbn"));
        tituloLabel.setText(messages.get("libro.titulo"));
        autorLabel.setText(messages.get("libro.autor"));
        categoriaLabel.setText(messages.get("libro.categoria"));
        cantidadLabel.setText(messages.get("libro.cantidad"));
        estadoLabel.setText(messages.get("libro.estado"));
    }

    private void guardarLibro() {
        String isbn = txtISBN.getText().trim();
        if (isbn.isEmpty() || !isbn.matches("^[0-9]{10,13}$")) {
            showAlert(messages.get("alert.error"), "ISBN inválido");
            return;
        }

        String existLibro = apiClient.fetchLibroByIsbn(isbn);
        if (existLibro != null && !existLibro.isEmpty()) {
            showAlert(messages.get("alert.error"), String.format(messages.get("alert.isbn.existe"), isbn));
            return;
        }

        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String estado = cmbEstadoUso.getSelectionModel().getSelectedItem();

        if (txtCantidad.getText().isEmpty()) {
            showAlert(messages.get("alert.error"), messages.get("alert.cantidad.invalida"));
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad < 0 || cantidad > 99) {
                showAlert(messages.get("alert.error"), messages.get("alert.cantidad.rango"));
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(messages.get("alert.error"), messages.get("alert.cantidad.numero"));
            return;
        }

        if (titulo.isEmpty() || autor.isEmpty() || categoria.isEmpty() || estado == null) {
            showAlert(messages.get("alert.error"), messages.get("alert.completa.campos"));
            return;
        }

        LibroDTO libroDTO = new LibroDTO();
        libroDTO.setIsbn(isbn);
        libroDTO.setTitulo(titulo);
        libroDTO.setAutor(autor);
        libroDTO.setCategoria(categoria);
        libroDTO.setEstadoUso("Disponible".equals(estado));

        try {
            LibroDTO response = apiClient.createLibro(libroDTO);
            showAlert(messages.get("alert.exito"), messages.get("alert.libro.anyadido"));
            if (onLibroCreado != null) {
                Libro libro = new Libro(response.getLibroId(), isbn, titulo, autor, categoria, estado);
                onLibroCreado.accept(libro);
            }
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert(messages.get("alert.error"), messages.get("alert.libro.noanyadido"));
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