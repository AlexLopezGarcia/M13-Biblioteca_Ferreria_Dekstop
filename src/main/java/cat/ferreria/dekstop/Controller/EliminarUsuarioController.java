package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.dtos.UsuarioDTO;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EliminarUsuarioController {

    @FXML private TextField dniField;
    @FXML private Button eliminarButton;

    private ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        eliminarButton.setOnAction(event -> eliminarUsuario());
    }

    @FXML
    private void eliminarUsuario() {
        String correo = dniField.getText().trim();

        if (correo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la eliminación", "Campo vacío", "Por favor, introduce un correo electrónico.");
            return;
        }

        if (!usuarioExiste(correo)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la eliminación", "Usuario no encontrado", "No se encontró un usuario con el correo proporcionado.");
            return;
        }

        boolean eliminado = apiClient.eliminarUsuarioEnAPI(correo);

        if (eliminado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminación exitosa", "Usuario eliminado correctamente",
                    "Correo: " + correo);
            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la eliminación", "No se pudo eliminar el usuario", "Hubo un problema al eliminar el usuario.");
        }
    }

    private boolean usuarioExiste(String correo) {
        String jsonUsuarios = apiClient.fetchUsuarios();
        if (jsonUsuarios != null) {
            Gson gson = new Gson();
            UsuarioDTO[] usuariosArray = gson.fromJson(jsonUsuarios, UsuarioDTO[].class);
            for (UsuarioDTO usuario : usuariosArray) {
                if (usuario.getCorreoElectronico().equals(correo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    private void limpiarCampos() {
        dniField.setText("");
    }
}

