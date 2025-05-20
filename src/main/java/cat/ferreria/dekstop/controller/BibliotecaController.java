package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.clazz.Libro;
import cat.ferreria.dekstop.model.dtos.LibroDTO;
import cat.ferreria.dekstop.vistas.*;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class BibliotecaController {
    private static final Logger _log = LoggerFactory.getLogger(BibliotecaController.class);

    @FXML TextField idField;
    @FXML private TextField isbnField;
    @FXML private TextField tituloField;
    @FXML private TextField autorField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private Button buscarButton;
    @FXML private Button btnAnyadir;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private Button btnRecargar;
    @FXML private Button btnRegistrarDevolucion;
    @FXML private Button btnRegistrarPrestamo;
    @FXML private Button btnLogarse;
    @FXML private Button btnRegistrarUsuario;
    @FXML private ComboBox<String> languageSelector;
    @FXML private Label isbnLabel;
    @FXML private Label tituloLabel;
    @FXML private Label autorLabel;
    @FXML private Label categoriaLabel;

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, String> colISBN;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, String> colEstado;

    private ObservableList<Libro> libros = FXCollections.observableArrayList();
    private ApiClient apiClient = new ApiClient();
    private Map<String, String> messages;
    private Function<String, Map<String, String>> messageFetcher;

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
        if (messages != null) {
            System.out.println("Mensajes cargados correctamente: " + messages);
            updateUI();
        } else {
            System.out.println("Los mensajes no se han cargado correctamente.");
        }
    }

    public void setMessageFetcher(Function<String, Map<String, String>> messageFetcher) {
        this.messageFetcher = messageFetcher;
    }

    @FXML
    public void initialize() {
        categoriaComboBox.setItems(FXCollections.observableArrayList("Narrativa", "Novela juvenil", "Bibliografía"));

        colISBN.setCellValueFactory(data -> data.getValue().isbnProperty());
        colTitulo.setCellValueFactory(data -> data.getValue().tituloProperty());
        colAutor.setCellValueFactory(data -> data.getValue().autorProperty());
        colCategoria.setCellValueFactory(data -> data.getValue().categoriaProperty());
        colEstado.setCellValueFactory(data -> data.getValue().estadoProperty());

        tablaLibros.setItems(libros);

        languageSelector.setItems(FXCollections.observableArrayList("es", "ca"));
        languageSelector.setValue("es");
        languageSelector.setOnAction(event -> {
            String selectedLang = languageSelector.getValue();
            try {
                messages = messageFetcher.apply(selectedLang);
                updateUI();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, messages != null ? messages.get("alert.error") : "Error", "No se pudo cambiar el idioma");
            }
        });

        try {
            messages = apiClient.fetchTranslations("es");
            updateUI();
        } catch (Exception e) {
            System.err.println("Error al cargar mensajes iniciales: " + e.getMessage());
        }

        cargarLibrosDesdeApi();

        btnAnyadir.setOnAction(event -> openPantallaCrearLibro());
        btnModificar.setOnAction(event -> openPantallaModificarLibro());
        buscarButton.setOnAction(event -> buscarLibros());
        btnEliminar.setOnAction(event -> eliminarLibro());
        btnRecargar.setOnAction(event -> recargarLibros());
        btnLogarse.setOnAction(event -> mostrarPantallaLogin());
        btnRegistrarPrestamo.setOnAction(event -> mostrarPantallaPrestamo());

    }

    private void updateUI() {
        if (messages == null) return;

        isbnLabel.setText(messages.get("libro.isbn"));
        tituloLabel.setText(messages.get("libro.titulo"));
        autorLabel.setText(messages.get("libro.autor"));
        categoriaLabel.setText(messages.get("libro.categoria"));

        btnRegistrarDevolucion.setText(messages.get("button.registrar.devolucion"));
        btnRegistrarPrestamo.setText(messages.get("button.registrar.prestamo"));
        btnEliminar.setText(messages.get("button.eliminar.libro"));
        btnModificar.setText(messages.get("button.modificar.libro"));
        btnAnyadir.setText(messages.get("button.anyadir.libro"));
        btnLogarse.setText(messages.get("button.logarse"));
        btnRegistrarUsuario.setText(messages.get("button.registrar.usuario"));
        buscarButton.setText(messages.get("button.buscar"));
        btnRecargar.setText(messages.get("button.recargar.lista"));

        colISBN.setText(messages.get("libro.isbn"));
        colTitulo.setText(messages.get("libro.titulo"));
        colAutor.setText(messages.get("libro.autor"));
        colCategoria.setText(messages.get("libro.categoria"));
        colEstado.setText(messages.get("libro.estado"));
    }

    private void cargarLibrosDesdeApi() {
        _log.info("Cargando libros desde la API...");
        String jsonLibros = apiClient.fetchAllLibros();
        if (jsonLibros != null && !jsonLibros.isEmpty()) {
            _log.info("Libros recibidos: {}", jsonLibros);
            try {
                Gson gson = new Gson();
                LibroDTO[] librosArray = gson.fromJson(jsonLibros, LibroDTO[].class);
                libros.clear();
                for (LibroDTO libroDTO : librosArray) {
                    if (libroDTO != null && libroDTO.getIsbn() != null) {
                        Libro libro = new Libro(
                                libroDTO.getLibroId(),
                                libroDTO.getIsbn(),
                                libroDTO.getTitulo(),
                                libroDTO.getAutor(),
                                libroDTO.getCategoria(),
                                libroDTO.getEstado()
                        );
                        libros.add(libro);
                        _log.info("Libro añadido: {}", libro.getTitulo());
                    } else {
                        _log.warn("LibroDTO nulo o sin ISBN: {}", libroDTO);
                    }
                }
                _log.info("Total libros cargados: {}", libros.size());
            } catch (Exception e) {
                _log.error("Error al deserializar libros: {}", e.getMessage(), e);
                showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), "Error al procesar los datos de los libros.");
            }
        } else {
            _log.error("Error al obtener los libros de la API: respuesta nula o vacía");
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), "No se pudo cargar la lista de libros.");
        }
    }

    private void buscarLibros() {
        _log.info("Buscando libros con ISBN: {}", isbnField.getText());
        String isbn = isbnField.getText().trim();
        if (!isbn.isEmpty()) {
            String jsonLibro = apiClient.fetchLibroByIsbn(isbn);
            if (jsonLibro != null && !jsonLibro.isEmpty()) {
                try {
                    Gson gson = new Gson();
                    LibroDTO libroDTO = gson.fromJson(jsonLibro, LibroDTO.class);
                    if (libroDTO != null && libroDTO.getIsbn() != null) {
                        Libro libro = new Libro(
                                libroDTO.getLibroId(),
                                libroDTO.getIsbn(),
                                libroDTO.getTitulo(),
                                libroDTO.getAutor(),
                                libroDTO.getCategoria(),
                                libroDTO.getEstado()
                        );
                        libros.clear();
                        libros.add(libro);
                        _log.info("Libro encontrado: {}", libro.getTitulo());
                    } else {
                        _log.warn("LibroDTO nulo o sin ISBN para ISBN: {}", isbn);
                        showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), "Libro no encontrado.");
                    }
                } catch (Exception e) {
                    _log.error("Error al deserializar libro: {}", e.getMessage(), e);
                    showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), "Error al procesar el libro.");
                }
            } else {
                _log.error("Error al buscar libro con ISBN: {}", isbn);
                showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), "No se pudo encontrar el libro.");
            }
        } else {
            cargarLibrosDesdeApi();
        }
    }

    private void recargarLibros() {
        cargarLibrosDesdeApi();
    }

    private void openPantallaCrearLibro() {
        PantallaCrearLibro pantallaCrearLibro = new PantallaCrearLibro(libro -> {
            libros.add(libro);
            cargarLibrosDesdeApi();
        }, messages);
        try {
            pantallaCrearLibro.show();
        } catch (Exception e) {
            _log.error("Error al abrir pantalla de crear libro: {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), "Error al abrir la pantalla de crear libro");
        }
    }

    private void openPantallaModificarLibro() {
        Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (libroSeleccionado == null) {
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), messages.get("alert.no.seleccionado"));
            return;
        }
        PantallaModificarLibro pantallaModificarLibro = new PantallaModificarLibro(libro -> cargarLibrosDesdeApi(), libroSeleccionado, messages);
        try {
            pantallaModificarLibro.show();
        } catch (Exception e) {
            _log.error("Error al abrir pantalla de modificar libro: {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), messages.get("alert.error.abrir.pantalla.modificarlibro"));
        }
    }

    private void eliminarLibro() {
        Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (libroSeleccionado == null) {
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), messages.get("alert.no.seleccionado"));
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle(messages.get("alert.confirmacion"));
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(messages.get("alert.confirmar.eliminar"));
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean eliminado = apiClient.eliminarLibroPorId(libroSeleccionado.getLibroId());
                if (eliminado) {
                    libros.remove(libroSeleccionado);
                    cargarLibrosDesdeApi();
                    _log.info("Libro eliminado: {}", libroSeleccionado.getTitulo());
                    showAlert(Alert.AlertType.INFORMATION, messages.get("alert.exito"), messages.get("alert.libro.eliminado"));
                }
            } catch (Exception e) {
                if (e.getMessage().contains("asociado a registros de historial")) {
                    Alert historialAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    historialAlert.setTitle(messages.get("alert.confirmacion"));
                    historialAlert.setHeaderText(null);
                    historialAlert.setContentText(messages.get("alert.libro.noeliminado.historial"));
                    Optional<ButtonType> historialResult = historialAlert.showAndWait();

                    if (historialResult.isPresent() && historialResult.get() == ButtonType.OK) {
                        try {
                            boolean eliminadoForzado = apiClient.eliminarLibroPorId(libroSeleccionado.getLibroId(), true);
                            if (eliminadoForzado) {
                                libros.remove(libroSeleccionado);
                                cargarLibrosDesdeApi();
                                _log.info("Libro con historial eliminado: {}", libroSeleccionado.getTitulo());
                                showAlert(Alert.AlertType.INFORMATION, messages.get("alert.exito"), messages.get("alert.libro.eliminado"));
                            }
                        } catch (Exception ex) {
                            _log.error("Error al eliminar el libro con historial: {}", ex.getMessage());
                            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), ex.getMessage());
                        }
                    }
                } else {
                    _log.error("Error al eliminar el libro: {}", e.getMessage());
                    showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), e.getMessage());
                }
            }
        }
    }

    private void mostrarPantallaLogin() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(messages.get("login.title"));
        dialog.setHeaderText(messages.get("login.header"));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        VBox content = new VBox(10, new Label(messages.get("login.username")), usernameField, new Label(messages.get("login.password")), passwordField);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        usernameField.textProperty().addListener((obs, old, newVal) -> {
            okButton.setDisable(newVal.trim().isEmpty() || passwordField.getText().trim().isEmpty());
        });
        passwordField.textProperty().addListener((obs, old, newVal) -> {
            okButton.setDisable(newVal.trim().isEmpty() || usernameField.getText().trim().isEmpty());
        });

        _log.info("Abriendo pantalla de inicio de sesión");

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            _log.info("Inicio de sesión cancelado por el usuario");
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), messages.get("alert.login.cancelado"));
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            _log.warn("Intento de login con campos vacíos");
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), messages.get("alert.completa.campos"));
            return;
        }

        String sanitizedUsername = username.trim().replaceAll("[^a-zA-Z0-9._-]", "");
        if (!sanitizedUsername.equals(username)) {
            _log.warn("Username contenía caracteres no permitidos: {}", username);
        }

        if (!sanitizedUsername.matches("^[a-zA-Z0-9._-]{3,20}$")) {
            _log.warn("Username inválido: {}", sanitizedUsername);
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), messages.get("alert.username.invalido"));
            return;
        }

        if (password.length() < 4 || password.length() > 50) {
            _log.warn("Longitud de contraseña inválida para usuario: {}", sanitizedUsername);
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), messages.get("alert.password.invalido"));
            return;
        }

        String token = apiClient.autenticar(sanitizedUsername, password);
        if (!token.isEmpty()) {
            _log.info("Login exitoso para usuario: {}", sanitizedUsername);
            cargarLibrosDesdeApi();
        } else {
            _log.warn("Login fallido para usuario: {}", sanitizedUsername);
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), "Login fallido");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void mostrarPantallaPrestamo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/pantallaPrestamo.fxml"));
            Parent root = loader.load();

            PrestamoController controller = loader.getController();
            controller.setMessages(messages);

            Stage stage = new Stage();
            stage.setTitle(messages.get("pantalla.prestamo.titulo"));
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            _log.error("Error al abrir la pantalla de préstamo: {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, messages.get("alert.error"), "No se pudo abrir la pantalla de préstamo.");
        }
    }

}