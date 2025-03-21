package cat.ferreria.dekstop.Controller;

import cat.ferreria.dekstop.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    private ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        lblError.setVisible(false);

        btnIniciarSesion.setOnAction(event -> iniciarSesion());

        lblOlvidado.setOnMouseClicked(event -> recuperarContrasena());
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        boolean credencialesValidas = apiClient.validarCredencialesEnAPI(correo, contrasena);

        if (credencialesValidas) {
            mostrarMensajeExito("Inicio de sesión exitoso.");
            lblError.setVisible(false);
        } else {
            lblError.setText("Correo o contraseña incorrectos");
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setVisible(true);
        }
    }

    private void mostrarMensajeExito(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Sesión iniciada");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void recuperarContrasena() {
        System.out.println("He olvidado mi contraseña clickeado");
    }
}






