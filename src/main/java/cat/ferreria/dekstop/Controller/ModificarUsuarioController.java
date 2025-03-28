package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.clazz.Usuario;
import cat.ferreria.dekstop.model.dtos.UsuarioDTO;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.regex.Pattern;

public class ModificarUsuarioController {

    @FXML private TextField dniField;
    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private PasswordField contrasenaField;
    @FXML private Button modificarButton;

    private ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        modificarButton.setOnAction(event -> modificarUsuario());
    }

    @FXML
    private void modificarUsuario() {
        String dni = dniField.getText().trim();
        String nombre = nombreField.getText().trim();
        String correo = correoField.getText().trim();
        String contrasena = contrasenaField.getText().trim();

        StringBuilder errores = new StringBuilder();

        if (dni.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            errores.append("Todos los campos deben estar rellenados.\n");
        }
        if (!dni.matches("^[0-9]{8}[A-Za-z]$")) {
            errores.append("- DNI incorrecto. Debe tener 8 números y 1 letra.\n");
        }
        if (nombre.length() < 2) {
            errores.append("- El nombre debe tener al menos 2 caracteres.\n");
        }
        String regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(regexCorreo, correo)) {
            errores.append("- Correo electrónico inválido.\n");
        }
        if (contrasena.length() < 8) {
            errores.append("- La contraseña debe tener al menos 8 caracteres.\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la modificación", "Se han encontrado errores", errores.toString());
            return;
        }

        if (!usuarioExiste(dni)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la modificación", "Usuario no encontrado", "No se encontró un usuario con el DNI proporcionado.");
            return;
        }

        Usuario usuarioModificado = new Usuario(dni, nombre, contrasena, correo);
        boolean modificado = apiClient.modificarUsuarioEnAPI(usuarioModificado);

        if (modificado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Modificación exitosa", "Usuario modificado correctamente",
                    "DNI: " + usuarioModificado.getDni() + "\n" +
                            "Nombre: " + usuarioModificado.getNombre() + "\n" +
                            "Correo: " + usuarioModificado.getCorreoElectronico());

            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la modificación", "No se pudo modificar el usuario", "Hubo un problema al modificar el usuario.");
        }
    }

    private boolean usuarioExiste(String dni) {
        String jsonUsuarios = apiClient.fetchUsuarios();
        if (jsonUsuarios != null) {
            Gson gson = new Gson();
            UsuarioDTO[] usuariosArray = gson.fromJson(jsonUsuarios, UsuarioDTO[].class);
            for (UsuarioDTO usuario : usuariosArray) {
                if (usuario.getDni().equals(dni)) {
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
        nombreField.setText("");
        correoField.setText("");
        contrasenaField.setText("");
    }
}

