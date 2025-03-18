package cat.ferreria.dekstop.Controller;

import cat.ferreria.dekstop.ApiClient;
import cat.ferreria.dekstop.bussines.Model.Usuario;
import cat.ferreria.dekstop.bussines.Model.UsuarioDTO;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegistrarUsuarioController {

    @FXML private Label labelDni, labelNombre, labelCorreo, labelContrasena, labelTitulo;
    @FXML private TextField dniField;
    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private PasswordField contrasenaField;
    @FXML private Button registrarButton;

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colDni;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;

    private ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
    private ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        registrarButton.setOnAction(event -> registrarUsuario());
    }

    @FXML
    private void registrarUsuario() {
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error en el registro", "Se han encontrado errores", errores.toString());
            return;
        }

        if (usuarioExiste(dni, correo)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Registro fallido", "El usuario ya existe", "El DNI o correo electrónico ya están registrados.");
            return;
        }

        // 🔧 Asegurar que los datos se pasan en el orden correcto
        Usuario nuevoUsuario = new Usuario(dni, nombre, contrasena, correo);
        boolean registrado = apiClient.registrarUsuarioEnAPI(nuevoUsuario);

        if (registrado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", "Usuario registrado correctamente",
                    "DNI: " + nuevoUsuario.getDni() + "\n" +
                            "Nombre: " + nuevoUsuario.getNombre() + "\n" +
                            "Correo: " + nuevoUsuario.getCorreoElectronico());

            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en el registro", "No se pudo registrar el usuario", "Hubo un problema al registrar el usuario.");
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









