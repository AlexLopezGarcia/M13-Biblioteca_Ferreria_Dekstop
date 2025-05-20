package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.dtos.LibroDTO;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DevolucionController {

    private static final Logger _log = LoggerFactory.getLogger(DevolucionController.class);
    private ApiClient apiClient = new ApiClient();
    private Map<String, String> messages;

    @FXML private TextField isbnField;
    @FXML private Button btnDevolver;
    @FXML private Label mensajeLabel;

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

    @FXML
    public void initialize() {
        btnDevolver.setOnAction(e -> devolverLibro());
    }

    private void devolverLibro() {
        String isbn = isbnField.getText().trim();

        if (isbn.isEmpty()) {
            mostrarMensaje("alert.id.invalido", Alert.AlertType.WARNING);
            return;
        }

        try {
            _log.info("Intentando devolver libro con ISBN '{}'", isbn);

            String jsonLibro = apiClient.fetchLibroByIsbn(isbn);
            if (jsonLibro == null || jsonLibro.isEmpty()) {
                mostrarMensaje("alert.libro.noencontrado", Alert.AlertType.ERROR);
                return;
            }

            Gson gson = new Gson();
            LibroDTO libroDTO = gson.fromJson(jsonLibro, LibroDTO.class);
            libroDTO.setEstado("disponible");

            apiClient.updateLibro(libroDTO);
            _log.info("Libro '{}' marcado como disponible", libroDTO.getTitulo());
            mostrarMensaje("alert.libro.devuelto", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            _log.error("Error al devolver libro: {}", e.getMessage(), e);
            mostrarMensaje("alert.error.estado", Alert.AlertType.ERROR);
        }
    }

    private void mostrarMensaje(String claveMensaje, Alert.AlertType tipo) {
        String mensaje = messages != null ? messages.getOrDefault(claveMensaje, claveMensaje) : claveMensaje;
        mensajeLabel.setText(mensaje);
        Alert alert = new Alert(tipo);
        alert.setTitle(messages != null ? messages.getOrDefault("alert.titulo", "Aviso") : "Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
