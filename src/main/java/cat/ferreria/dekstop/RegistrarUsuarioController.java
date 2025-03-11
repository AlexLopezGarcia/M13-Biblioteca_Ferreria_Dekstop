package cat.ferreria.dekstop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.regex.Pattern;

public class RegistrarUsuarioController {

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
        if (contrasena.length() < 6) {
            errores.append("- La contraseña debe tener al menos 6 caracteres.\n");
        }

        if (errores.length() > 0) {
            // Mostrar un Alert en lugar de JOptionPane
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error en el registro");
            alerta.setHeaderText("Se han encontrado errores");
            alerta.setContentText(errores.toString());
            alerta.showAndWait(); // Mantiene el foco en la ventana actual
        } else {
            Usuario nuevoUsuario = new Usuario(dni, nombre, contrasena, correo);

            Alert exito = new Alert(AlertType.INFORMATION);
            exito.setTitle("Registro exitoso");
            exito.setHeaderText("Usuario registrado correctamente");
            exito.setContentText("DNI: " + nuevoUsuario.getDni() + "\n" +
                    "Nombre: " + nuevoUsuario.getNombre() + "\n" +
                    "Correo: " + nuevoUsuario.getCorreoElectronico());
            exito.showAndWait();

            limpiarCampos();
        }
    }



    private void limpiarCampos() {
        dniField.setText("");
        nombreField.setText("");
        correoField.setText("");
        contrasenaField.setText("");
    }
}







