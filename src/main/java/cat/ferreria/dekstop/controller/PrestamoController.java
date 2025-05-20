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
    private ApiClient apiClient = new ApiClient();
    private Map<String, String> messages;

    @FXML private TextField libroIdField;
    @FXML private Button btnPrestar;
    @FXML private Button btnDevolver;
    @FXML private Label mensajeLabel;

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

    @FXML
    public void initialize() {
        btnPrestar.setOnAction(e -> cambiarEstadoLibro("prestado"));
        btnDevolver.setOnAction(e -> cambiarEstadoLibro("disponible"));
    }

    private void cambiarEstadoLibro(String nuevoEstado) {
        try {
            int idLibro = Integer.parseInt(libroIdField.getText().trim());
            _log.info("Solicitando cambio de estado a '{}' para libro con ID {}", nuevoEstado, idLibro);

            // Paso 1: obtener libro por ISBN (usamos ID como ISBN aquí)
            String jsonLibro = apiClient.fetchLibroByIsbn(String.valueOf(idLibro));
            if (jsonLibro == null || jsonLibro.isEmpty()) {
                mostrarMensaje("alert.libro.noencontrado", Alert.AlertType.ERROR);
                return;
            }

            // Paso 2: modificar estado
            Gson gson = new Gson();
            LibroDTO libroDTO = gson.fromJson(jsonLibro, LibroDTO.class);
            libroDTO.setEstado(nuevoEstado);

            // Paso 3: enviar actualización
            apiClient.updateLibro(libroDTO);
            _log.info("Estado del libro '{}' actualizado a '{}'", libroDTO.getTitulo(), nuevoEstado);

            if (nuevoEstado.equals("prestado")) {
                mostrarMensaje("alert.prestamo.realizado", Alert.AlertType.INFORMATION);
            } else {
                mostrarMensaje("alert.libro.devuelto", Alert.AlertType.INFORMATION);
            }

        } catch (NumberFormatException e) {
            _log.warn("ID de libro inválido: {}", libroIdField.getText());
            mostrarMensaje("alert.id.invalido", Alert.AlertType.WARNING);
        } catch (Exception e) {
            _log.error("Error al cambiar estado del libro: {}", e.getMessage(), e);
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
