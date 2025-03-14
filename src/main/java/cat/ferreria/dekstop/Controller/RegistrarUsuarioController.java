package cat.ferreria.dekstop.Controller;

import cat.ferreria.dekstop.bussines.Model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
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

    @FXML
    public void initialize() {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.messages");
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

        // Verificar si el usuario ya existe en la base de datos
        if (usuarioExiste(dni, correo)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Registro fallido", "El usuario ya existe", "El DNI o correo electrónico ya están registrados.");
            return;
        }

        // Aquí iría la llamada a la API para registrar el usuario (POST)
        Usuario nuevoUsuario = new Usuario(dni, nombre, contrasena, correo);
        usuarios.add(nuevoUsuario);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", "Usuario registrado correctamente",
                "DNI: " + nuevoUsuario.getDni() + "\n" +
                        "Nombre: " + nuevoUsuario.getNombre() + "\n" +
                        "Correo: " + nuevoUsuario.getCorreoElectronico());

        limpiarCampos();
    }

    private boolean usuarioExiste(String dni, String correo) {
        try {
            String urlString = "http://tu-api.com/usuarios?dni=" + dni + "&correo=" + correo;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // Si el usuario ya existe, la API debería responder con un 200
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder jsonString = new StringBuilder();
                while (scanner.hasNext()) {
                    jsonString.append(scanner.nextLine());
                }
                scanner.close();

                return !jsonString.toString().isEmpty(); // Si la respuesta no está vacía, el usuario existe
            }
        } catch (IOException e) {
            e.printStackTrace();
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








