package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.clazz.Libro;
import cat.ferreria.dekstop.model.dtos.LibroDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

public class ModificarLibroController {

    @FXML private TextField txtISBN;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtCategoria;
    @FXML private ComboBox<String> cmbEstadoUso;
    @FXML private Button btnGuardar;
    @FXML private Label isbnLabel;
    @FXML private Label tituloLabel;
    @FXML private Label autorLabel;
    @FXML private Label categoriaLabel;
    @FXML private Label estadoLabel;

    private ApiClient apiClient = new ApiClient();
    private Consumer<Libro> onLibroModificado;
    private Libro libroSeleccionado;
    private Map<String, String> messages;

    @FXML
    private void initialize() {
        cmbEstadoUso.getItems().addAll("Disponible", "Prestado");
        btnGuardar.setOnAction(event -> guardarLibro());

    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
        updateUI();
    }

    public void setOnLibroModificado(Consumer<Libro> onLibroModificado) {
        this.onLibroModificado = onLibroModificado;
    }

    public void setLibroSeleccionado(Libro libroSeleccionado) {
        this.libroSeleccionado = libroSeleccionado;
        populateFields();
    }

    private void populateFields() {
        if (libroSeleccionado == null){
            return;
        }

        txtISBN.setText(libroSeleccionado.getIsbn());
        txtISBN.setDisable(true);
        txtTitulo.setText(libroSeleccionado.getTitulo());
        txtAutor.setText(libroSeleccionado.getAutor());
        txtCategoria.setText(libroSeleccionado.getCategoria());
        cmbEstadoUso.setValue(libroSeleccionado.getEstado());
    }

    private void updateUI() {
        if (messages == null) return;
        btnGuardar.setText(messages.get("button.modificar.libro"));
        isbnLabel.setText(messages.get("libro.isbn"));
        tituloLabel.setText(messages.get("libro.titulo"));
        autorLabel.setText(messages.get("libro.autor"));
        categoriaLabel.setText(messages.get("libro.categoria"));
        estadoLabel.setText(messages.get("libro.estado"));
    }

    private void guardarLibro() {
        String isbn = txtISBN.getText().trim();
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String estado = cmbEstadoUso.getValue();

        if (titulo.isEmpty() || autor.isEmpty() || categoria.isEmpty() || estado == null) {
            showAlert(messages.get("alert.error"), messages.get("alert.completa.campos"));
            return;
        }

        LibroDTO dto = new LibroDTO();
        dto.setLibroId(libroSeleccionado.getLibroId());
        dto.setIsbn(isbn);
        dto.setTitulo(titulo);
        dto.setAutor(autor);
        dto.setCategoria(categoria);
        dto.setEstadoUso("Disponible".equals(estado));

        try {
            LibroDTO actualizado = apiClient.updateLibro(dto);
            showAlert(messages.get("alert.exito"), messages.get("alert.exito"));
            if (onLibroModificado != null) {
                Libro libro = new Libro(actualizado.getLibroId(), isbn, titulo, autor, categoria, estado);
                onLibroModificado.accept(libro);
            }
            ((Stage) btnGuardar.getScene().getWindow()).close();
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