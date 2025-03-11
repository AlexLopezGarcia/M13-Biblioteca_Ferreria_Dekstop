package cat.ferreria.dekstop;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.swing.JOptionPane;
import java.util.regex.Pattern;

public class RegistrarUsuarioController {

    @FXML private TextField dniField;
    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private PasswordField contrasenaField;
    @FXML private Button registrarButton;

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
            JOptionPane.showMessageDialog(null, "Errores encontrados:\n" + errores, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            Usuario nuevoUsuario = new Usuario(dni, nombre, contrasena, correo);
            JOptionPane.showMessageDialog(null, "Usuario registrado correctamente:\n" +
                            "DNI: " + nuevoUsuario.getDni() + "\n" +
                            "Nombre: " + nuevoUsuario.getNombre() + "\n" +
                            "Correo: " + nuevoUsuario.getCorreoElectronico(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
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






