package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SesionController {

    private BibliotecaController bibliotecaController;

    public void setBibliotecaController(BibliotecaController controller) {
        this.bibliotecaController = controller;
    }

    @FXML
    private TextField correoTextField;

    @FXML
    private PasswordField contrasenaTextField;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Label errorLabel;

    @FXML private ComboBox<String> usuarioComboBox;

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

            // Muestra los botones desde el controlador principal
            if (bibliotecaController != null) {
                bibliotecaController.mostrarBotonesUsuario();
            }

            // Cierra la ventana de sesión
            ((Stage) btnIniciarSesion.getScene().getWindow()).close();
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
