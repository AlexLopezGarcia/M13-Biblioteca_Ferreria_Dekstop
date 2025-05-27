package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.clazz.Idioma;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class BibliotecaController {
    private static final Logger _log = LoggerFactory.getLogger(BibliotecaController.class);

    @FXML private TextField emailField;
    @FXML private TextArea messageField;
    @FXML private Label emailStatusLabel;

    private EmailController emailController = new EmailController();

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
    @FXML private ComboBox<Idioma> languageSelector;
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
            updateUI();
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

        configurarComboBoxIdiomas();

        cargarLibrosDesdeApi();
        updateUI();

        btnAnyadir.setOnAction(event -> openPantallaCrearLibro());
        btnModificar.setOnAction(event -> abrirModificarUsuario());
        buscarButton.setOnAction(event -> buscarLibros());
        btnEliminar.setOnAction(event -> eliminarLibro());
        btnRecargar.setOnAction(event -> recargarLibros());
        btnLogarse.setOnAction(event -> abrirPanelSesion());
        btnRegistrarUsuario.setOnAction(event -> abrirRegistroUsuario());
    }

    private void configurarComboBoxIdiomas() {
        Idioma cat = new Idioma("ca", "Català", new Image(getClass().getResource("/img/catalunya.png").toExternalForm()));
        Idioma esp = new Idioma("es", "Español", new Image(getClass().getResource("/img/espana.png").toExternalForm()));

        languageSelector.setItems(FXCollections.observableArrayList(cat, esp));
        languageSelector.getSelectionModel().select(esp);

        languageSelector.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Idioma> call(ListView<Idioma> param) {
                return new ListCell<>() {
                    private final ImageView imageView = new ImageView();
                    @Override
                    protected void updateItem(Idioma item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            imageView.setImage(item.getIcono());
                            imageView.setFitWidth(24);
                            imageView.setFitHeight(16);
                            setText(item.getNombre());
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        languageSelector.setButtonCell(languageSelector.getCellFactory().call(null));

        languageSelector.setOnAction(event -> {
            Idioma idiomaSeleccionado = languageSelector.getValue();
            if (idiomaSeleccionado != null) {
                try {
                    messages = messageFetcher.apply(idiomaSeleccionado.getCodigo());
                    updateUI();
                } catch (Exception e) {
                    showAlert("Error", "No se pudo cambiar el idioma.");
                }
            }
        });
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
            try {
                Gson gson = new Gson();
                LibroDTO[] librosArray = gson.fromJson(jsonLibros, LibroDTO[].class);
                libros.clear();
                for (LibroDTO libroDTO : librosArray) {
                    if (libroDTO != null && libroDTO.getIsbn() != null) {
                        Libro libro = new Libro(
                                libroDTO.getLibroId(), libroDTO.getIsbn(), libroDTO.getTitulo(),
                                libroDTO.getAutor(), libroDTO.getCategoria(), libroDTO.getEstado());
                        libros.add(libro);
                    }
                }
            } catch (Exception e) {
                _log.error("Error al deserializar libros: {}", e.getMessage(), e);
                showAlert(messages.get("alert.error"), "Error al procesar los datos de los libros.");
            }
        } else {
            showAlert(messages.get("alert.error"), "No se pudo cargar la lista de libros.");
        }
    }

    private void buscarLibros() {
        String isbn = isbnField.getText().trim();
        if (!isbn.isEmpty()) {
            String jsonLibro = apiClient.fetchLibroByIsbn(isbn);
            if (jsonLibro != null && !jsonLibro.isEmpty()) {
                try {
                    Gson gson = new Gson();
                    LibroDTO libroDTO = gson.fromJson(jsonLibro, LibroDTO.class);
                    if (libroDTO != null && libroDTO.getIsbn() != null) {
                        Libro libro = new Libro(libroDTO.getLibroId(), libroDTO.getIsbn(), libroDTO.getTitulo(),
                                libroDTO.getAutor(), libroDTO.getCategoria(), libroDTO.getEstado());
                        libros.clear();
                        libros.add(libro);
                    } else {
                        showAlert(messages.get("alert.error"), "Libro no encontrado.");
                    }
                } catch (Exception e) {
                    showAlert(messages.get("alert.error"), "Error al procesar el libro.");
                }
            } else {
                showAlert(messages.get("alert.error"), "No se pudo encontrar el libro.");
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
            showAlert(messages.get("alert.error"), "Error al abrir la pantalla de crear libro");
        }
    }
    @FXML
    private void abrirModificarUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modificarUsuario.fxml"));
            Parent root = loader.load();
            ModificarUsuarioController controller = loader.getController();
            controller.setMessages(messages);
            Stage stage = new Stage();
            stage.setTitle("Modificar Usuario");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void abrirRegistroUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registrarUsuario.fxml"));
            Parent root = loader.load();
            RegistrarUsuarioController controller = loader.getController();
            controller.setMessages(messages);
            Stage stage = new Stage();
            stage.setTitle(messages.getOrDefault("form.registro", "Registro de Usuario"));
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo abrir la pantalla para registrar usuario.");
        }
    }
    @FXML
    private void abrirPanelSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sesion.fxml"));
            Parent root = loader.load();
            SesionController sesionController = loader.getController();
            sesionController.setMessages(messages);
            sesionController.setBibliotecaController(this);
            Stage stage = new Stage();
            stage.setTitle("Iniciar Sesión");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo abrir el panel de sesión.");
        }
    }

    private void eliminarLibro() {
        Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (libroSeleccionado == null) {
            showAlert(messages.get("alert.error"), messages.get("alert.no.seleccionado"));
            return;
        }
        try {
            boolean eliminado = apiClient.eliminarLibroPorId(libroSeleccionado.getLibroId());
            if (eliminado) {
                libros.remove(libroSeleccionado);
                cargarLibrosDesdeApi();
            } else {
                showAlert(messages.get("alert.error"), "No se pudo eliminar el libro");
            }
        } catch (Exception e) {
            if (e.getMessage().contains("asociado a registros de historial")) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setContentText(messages.get("alert.libro.noeliminado.historial"));
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        boolean eliminadoForzado = apiClient.eliminarLibroPorId(libroSeleccionado.getLibroId(), true);
                        if (eliminadoForzado) {
                            libros.remove(libroSeleccionado);
                            cargarLibrosDesdeApi();
                        }
                    } catch (Exception ex) {
                        showAlert(messages.get("alert.error"), ex.getMessage());
                    }
                }
            } else {
                showAlert(messages.get("alert.error"), e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void mostrarBotonesUsuario() {
        btnRegistrarUsuario.setVisible(true);
    }
}