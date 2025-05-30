package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.dtos.LibroDTO;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PrestamoController {

    private static final Logger _log = LoggerFactory.getLogger(PrestamoController.class);
    private final ApiClient apiClient = new ApiClient();
    private Map<String, String> messages;

    @FXML private TextField libroIdField;
    @FXML private Button btnPrestar;
    @FXML private Label mensajeLabel;

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

    @FXML
    public void initialize() {
        btnPrestar.setOnAction(e -> prestarLibro());
    }

    private void prestarLibro() {
        try {
            String isbn = libroIdField.getText().trim();
            _log.info("Buscando libro con ISBN {}", isbn);

            String jsonLibro = apiClient.fetchLibroByIsbn(isbn);
            if (jsonLibro == null || jsonLibro.isEmpty()) {
                mostrarMensaje("Libro no encontrado", Alert.AlertType.ERROR);
                return;
            }

            Gson gson = new Gson();
            LibroDTO libroDTO = gson.fromJson(jsonLibro, LibroDTO.class);

            if (!libroDTO.getEstadoUso()) {
                mostrarMensaje("Este libro ya está prestado", Alert.AlertType.WARNING);
                return;
            }

            libroDTO.setEstadoUso(false); // marcar como prestado
            apiClient.updateLibro(libroDTO);

            mostrarMensaje("Libro prestado correctamente", Alert.AlertType.INFORMATION);
            _log.info("Libro {} prestado", libroDTO.getTitulo());

        } catch (Exception e) {
            _log.error("Error al prestar libro: {}", e.getMessage(), e);
            mostrarMensaje("Error al prestar el libro", Alert.AlertType.ERROR);
        }
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        mensajeLabel.setText(mensaje);
        Alert alert = new Alert(tipo);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
