package cat.ferreria.dekstop.Controller;

import cat.ferreria.dekstop.bussines.Model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SesionController {

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Label lblError;

    @FXML
    private Label lblOlvidado;

    private final List<Usuario> usuarios = new ArrayList<>();

    @FXML
    public void initialize() {
        lblError.setVisible(false);

        usuarios.add(new Usuario("12345678X", "Juan Pérez", "1234", "juan@example.com"));

        btnIniciarSesion.setOnAction(event -> iniciarSesion());

        lblOlvidado.setOnMouseClicked(this::recuperarContrasena);
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        boolean usuarioValido = usuarios.stream()
                .anyMatch(u -> u.getCorreoElectronico().equals(correo) && u.getContrasena().equals(contrasena));

        if (usuarioValido) {
            lblError.setText("Inicio de sesión exitoso");
            lblError.setTextFill(Color.GREEN);
            lblError.setVisible(true);
            lblOlvidado.setVisible(false);
        } else {
            lblError.setText("Correo o contraseña incorrectos");
            lblError.setTextFill(Color.RED);
            lblError.setVisible(true);
            lblOlvidado.setVisible(true);
        }
    }

    private void recuperarContrasena(MouseEvent event) {
        System.out.println("He olvidado mi contraseña clickeado");
    }
}


