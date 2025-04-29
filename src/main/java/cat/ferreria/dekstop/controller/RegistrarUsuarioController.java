package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.clazz.Usuario;
import cat.ferreria.dekstop.model.dtos.UsuarioDTO;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Map;
import java.util.regex.Pattern;

public class RegistrarUsuarioController {

    @FXML private TextField dniField;
    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private PasswordField contrasenaField;
    @FXML private Button registrarButton;
    @FXML private Label labelDni;
    @FXML private Label labelFormulario;
    @FXML private Label labelNombre;
    @FXML private Label labelCorreo;
    @FXML private Label labelContrasena;

    private ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        registrarButton.setOnAction(event -> registrarUsuario());
    }

    private Map<String, String> messages;

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
        updateUI();
    }

    private void updateUI() {
        if (messages == null) return;

        labelFormulario.setText(messages.get("label.registro"));
        labelDni.setText(messages.get("label.dni"));
        labelNombre.setText(messages.get("label.nombre"));
        labelCorreo.setText(messages.get("label.correo"));
        labelContrasena.setText(messages.get("label.contrasena"));
        registrarButton.setText(messages.get("button.registrar.usuario"));
    }


    @FXML
    private void registrarUsuario() {
        String dni = dniField.getText().trim();
        String nombre = nombreField.getText().trim();
        String correo = correoField.getText().trim();
        String contrasena = contrasenaField.getText().trim();

        StringBuilder errores = new StringBuilder();

        if (dni.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            errores.append("- ").append(messages.get("validacion.campos.vacios")).append("\n");
        }
        if (!dni.matches("^[0-9]{8}[A-Za-z]$")) {
            errores.append("- ").append(messages.get("validacion.dni")).append("\n");
        }
        if (nombre.length() < 2 || nombre.length() > 30) {
            errores.append("- ").append(messages.get("validacion.nombre")).append("\n");
        }
        String regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(regexCorreo, correo)) {
            errores.append("- ").append(messages.get("validacion.correo")).append("\n");
        }
        if (contrasena.length() < 8) {
            errores.append("- ").append(messages.get("validacion.contrasena")).append("\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    messages.get("alert.error"),
                    messages.get("alert.registro.fallido"),
                    errores.toString());
            return;
        }

        if (usuarioExiste(dni, correo)) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    messages.get("alert.registro.fallido"),
                    messages.get("alert.usuario.existe"),
                    messages.get("alert.usuario.duplicado"));
            return;
        }

        Usuario nuevoUsuario = new Usuario(dni, nombre, contrasena, correo);
        boolean registrado = apiClient.registrarUsuarioEnAPI(nuevoUsuario);

        if (registrado) {
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    messages.get("alert.exito"),
                    messages.get("alert.registro.exitoso"),
                    "DNI: " + nuevoUsuario.getDni() + "\n" +
                            messages.get("label.nombre") + ": " + nuevoUsuario.getNombre() + "\n" +
                            messages.get("label.correo") + ": " + nuevoUsuario.getCorreoElectronico());
            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR,
                    messages.get("alert.error"),
                    messages.get("alert.registro.fallido"),
                    messages.get("alert.registro.error"));
        }
    }

    private boolean usuarioExiste(String dni, String correo) {
        String jsonUsuarios = apiClient.fetchUsuarios();
        boolean existe = false;

        if (jsonUsuarios != null) {
            Gson gson = new Gson();
            UsuarioDTO[] usuariosArray = gson.fromJson(jsonUsuarios, UsuarioDTO[].class);

            for (UsuarioDTO usuario : usuariosArray) {
                if (usuario.getDni().equals(dni) || usuario.getCorreoElectronico().equals(correo)) {
                    existe = true;
                    break;
                }
            }
        }
        return existe;
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