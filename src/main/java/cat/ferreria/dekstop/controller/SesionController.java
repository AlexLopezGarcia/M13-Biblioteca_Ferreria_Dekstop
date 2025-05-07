
package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SesionController {

    @FXML private TextField correoTextField;
    @FXML private PasswordField contrasenaTextField;
    @FXML private Button btnIniciarSesion;
    @FXML private Label errorLabel;
    @FXML private Label olvidadoLabel;
    @FXML private Label labelCorreo;
    @FXML private Label labelContrasena;
    @FXML private Label labelIniciarSesion;
    private final Logger _log = LoggerFactory.getLogger(SesionController.class);
    private final Gson gson = new Gson();

    private ApiClient apiClient = new ApiClient();
    private BibliotecaController bibliotecaController;
    private Map<String, String> messages;

    public void setBibliotecaController(BibliotecaController controller) {
        this.bibliotecaController = controller;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
            updateUI();
    }

    private void updateUI(){
        if (messages == null) return;
        labelIniciarSesion.setText(messages.get("label.iniciar.sesion"));
        labelCorreo.setText(messages.get("label.correo.sesion"));
        btnIniciarSesion.setText(messages.get("button.iniciar.sesion"));
        labelContrasena.setText(messages.get("label.contrasena.sesion"));
        errorLabel.setText(messages.get("label.error"));
        olvidadoLabel.setText(messages.get("label.olvidado"));
        _log.info(gson.toJson(messages));

    }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);

        btnIniciarSesion.setOnAction(event -> iniciarSesion());
        olvidadoLabel.setOnMouseClicked(event -> recuperarContrasena());
    }

    @FXML
    private void iniciarSesion() {
        String correo = correoTextField.getText().trim();
        String contrasena = contrasenaTextField.getText().trim();

        boolean credencialesValidas = apiClient.validarCredencialesEnAPI(correo, contrasena);

        if (credencialesValidas) {
            mostrarMensajeExito(messages.get("alert.inicio.sesion"));
            errorLabel.setVisible(false);

            if (bibliotecaController != null) {
                bibliotecaController.mostrarBotonesUsuario();
            }

            ((Stage) btnIniciarSesion.getScene().getWindow()).close();
        } else {
            errorLabel.setText(messages.get("label.error"));
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
        }
    }

    private void mostrarMensajeExito(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void recuperarContrasena() {
        // Por implementar
    }
}
