package cat.ferreria.dekstop.Controller;

import cat.ferreria.dekstop.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SesionController {

    @FXML
    private TextField correoTextField;

    @FXML
    private PasswordField contrasenaTextField;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Label errorLabel;

    @FXML
    private Label olvidadoLabel;

    private ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);

        btnIniciarSesion.setOnAction(event -> iniciarSesion());

        olvidadoLabel.setOnMouseClicked(event -> recuperarContrasena());
    }

    private void iniciarSesion() {
        String correo = correoTextField.getText().trim();
        String contrasena = contrasenaTextField.getText().trim();

        boolean credencialesValidas = apiClient.validarCredencialesEnAPI(correo, contrasena);

        if (credencialesValidas) {
            mostrarMensajeExito("Inicio de sesión exitoso.");
            errorLabel.setVisible(false);
        } else {
            errorLabel.setText("Correo o contraseña incorrectos");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
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

    }
}






