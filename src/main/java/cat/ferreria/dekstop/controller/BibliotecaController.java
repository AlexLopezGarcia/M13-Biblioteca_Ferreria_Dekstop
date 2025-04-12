package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.dtos.HistorialDTO;
import cat.ferreria.dekstop.model.clazz.Libro;
import cat.ferreria.dekstop.model.dtos.LibroDTO;
import cat.ferreria.dekstop.vistas.PantallaCrearLibro;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

public class BibliotecaController {

    // Clase interna para manejar idiomas con icono
    public static class Idioma {
        private final String codigo;
        private final String nombre;
        private final Image icono;

        public Idioma(String codigo, String nombre, Image icono) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.icono = icono;
        }

        public String getCodigo() { return codigo; }

        public String getNombre() { return nombre; }

        public Image getIcono() { return icono; }

        @Override
        public String toString() {
            return nombre;
        }
    }

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
    @FXML private ComboBox<Idioma> languageSelector;
    @FXML private ComboBox<String> usuarioComboBox;
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
        updateUI();
    }

    public void setMessageFetcher(Function<String, Map<String, String>> messageFetcher) {
        this.messageFetcher = messageFetcher;
    }

    @FXML
    public void initialize() {
        // Configurar el ComboBox de categorías
        categoriaComboBox.setItems(FXCollections.observableArrayList(
                "Narrativa", "Novela juvenil", "Bibliografía"
        ));

        // Configurar columnas de la tabla
        colISBN.setCellValueFactory(data -> data.getValue().isbnProperty());
        colTitulo.setCellValueFactory(data -> data.getValue().tituloProperty());
        colAutor.setCellValueFactory(data -> data.getValue().autorProperty());
        colCategoria.setCellValueFactory(data -> data.getValue().categoriaProperty());
        colEstado.setCellValueFactory(data -> data.getValue().estadoProperty());

        tablaLibros.setItems(libros);

        // Cargar libros desde API
        cargarLibrosDesdeApi();

        // Configurar ComboBox de idiomas con banderas
        configurarComboBoxIdiomas();

        // Botones
        btnAnyadir.setOnAction(event -> openPantallaCrearLibro());
        buscarButton.setOnAction(event -> buscarLibros());
        btnEliminar.setOnAction(event -> eliminarLibro());
        btnRecargar.setOnAction(event -> recargarLibros());
    }

    private void configurarComboBoxIdiomas() {
        Idioma cat = new Idioma("ca", "Català", new Image(getClass().getResource("/img/catalunya.png").toExternalForm()));
        Idioma esp = new Idioma("es", "Español", new Image(getClass().getResource("/img/espana.png").toExternalForm()));

        languageSelector.setItems(FXCollections.observableArrayList(cat, esp));
        languageSelector.getSelectionModel().select(esp); // Español por defecto

        // Mostrar icono y nombre
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

        // Mostrar icono también en el botón cerrado
        languageSelector.setButtonCell(languageSelector.getCellFactory().call(null));

        // Cambiar idioma cuando se selecciona uno nuevo
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
        buscarButton.setText(messages.get("button.buscar"));
        btnRecargar.setText(messages.get("button.recargar.lista"));

        colISBN.setText(messages.get("libro.isbn"));
        colTitulo.setText(messages.get("libro.titulo"));
        colAutor.setText(messages.get("libro.autor"));
        colCategoria.setText(messages.get("libro.categoria"));
        colEstado.setText(messages.get("libro.estado"));
    }

    private void cargarLibrosDesdeApi() {
        String jsonLibros = apiClient.fetchAllLibros();
        if (jsonLibros != null) {
            Gson gson = new Gson();
            LibroDTO[] librosArray = gson.fromJson(jsonLibros, LibroDTO[].class);
            libros.clear();
            for (LibroDTO dto : librosArray) {
                if (dto != null && dto.getIsbn() != null) {
                    libros.add(new Libro(dto.getIsbn(), dto.getTitulo(), dto.getAutor(), dto.getCategoria(), dto.getEstado()));
                }
            }
        } else {
            showAlert("Error", "No se pudo cargar la lista de libros.");
        }
    }

    private void buscarLibros() {
        System.out.println("Buscando libros con ISBN: " + isbnField.getText());
    }

    private void recargarLibros() {
        cargarLibrosDesdeApi();
    }

    private void openPantallaCrearLibro() {
        PantallaCrearLibro pantalla = new PantallaCrearLibro(libro -> {
            libros.add(libro);
            cargarLibrosDesdeApi();
        }, messages);
        try {
            pantalla.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo abrir la pantalla para crear un libro.");
        }
    }
    @FXML
    private void abrirRegistroUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registrarUsuario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registro de Usuario");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirPanelSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sesion.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Iniciar Sesión");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void accionUsuario() {
        String opcionSeleccionada = usuarioComboBox.getValue();
        if (opcionSeleccionada == null) {
            return;
        }

        switch (opcionSeleccionada) {
            case "Registrar Usuario":
                abrirRegistroUsuario();
                break;
            case "Modificar Usuario":
                abrirModificarUsuario();
                break;
        }
    }

    // Métodos que abren las respectivas pantallas
    @FXML
    private void abrirModificarUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modificarUsuario.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Modificar Usuario");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eliminarLibro() {
        Libro libro = tablaLibros.getSelectionModel().getSelectedItem();
        if (libro != null) {
            boolean eliminado = apiClient.eliminarLibroPorIsbn(libro.getIsbn());
            if (eliminado) {
                libros.remove(libro);
                cargarLibrosDesdeApi();
            } else {
                showAlert("Error", "No se pudo eliminar el libro.");
            }
        } else {
            showAlert("Error", "No se ha seleccionado ningún libro.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
