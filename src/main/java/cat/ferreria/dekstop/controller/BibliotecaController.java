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
import javafx.scene.control.*;

import java.util.Map;
import java.util.function.Function;

public class BibliotecaController {

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

        // Configurar las columnas de la tabla
        colISBN.setCellValueFactory(data -> data.getValue().isbnProperty());
        colTitulo.setCellValueFactory(data -> data.getValue().tituloProperty());
        colAutor.setCellValueFactory(data -> data.getValue().autorProperty());
        colCategoria.setCellValueFactory(data -> data.getValue().categoriaProperty());
        colEstado.setCellValueFactory(data -> data.getValue().estadoProperty());

        tablaLibros.setItems(libros);

        // Cargar libros desde la API
        cargarLibrosDesdeApi();

        // Configurar el ComboBox de idiomas
        languageSelector.setItems(FXCollections.observableArrayList("es", "ca"));
        languageSelector.setValue("es");
        languageSelector.setOnAction(event -> {
            String selectedLang = languageSelector.getValue();
            try {
                messages = messageFetcher.apply(selectedLang);
                updateUI();
            } catch (Exception e) {
                showAlert(messages.get("alert.error"), "No se pudo cambiar el idioma");
            }
        });

        // Configurar acciones de los botones
        btnAnyadir.setOnAction(event -> openPantallaCrearLibro());
        buscarButton.setOnAction(event -> buscarLibros());
        btnEliminar.setOnAction(event -> eliminarLibro());
        btnRecargar.setOnAction(event -> recargarLibros());
    }

    private void updateUI() {
        if (messages == null) return;

        // Actualizar etiquetas
        isbnLabel.setText(messages.get("libro.isbn"));
        tituloLabel.setText(messages.get("libro.titulo"));
        autorLabel.setText(messages.get("libro.autor"));
        categoriaLabel.setText(messages.get("libro.categoria"));

        // Actualizar botones
        btnRegistrarDevolucion.setText(messages.get("button.registrar.devolucion"));
        btnRegistrarPrestamo.setText(messages.get("button.registrar.prestamo"));
        btnEliminar.setText(messages.get("button.eliminar.libro"));
        btnModificar.setText(messages.get("button.modificar.libro"));
        btnAnyadir.setText(messages.get("button.anyadir.libro"));
        btnLogarse.setText(messages.get("button.logarse"));
        btnRegistrarUsuario.setText(messages.get("button.registrar.usuario"));
        buscarButton.setText(messages.get("button.buscar"));
        btnRecargar.setText(messages.get("button.recargar.lista"));

        // Actualizar columnas de la tabla
        colISBN.setText(messages.get("libro.isbn"));
        colTitulo.setText(messages.get("libro.titulo"));
        colAutor.setText(messages.get("libro.autor"));
        colCategoria.setText(messages.get("libro.categoria"));
        colEstado.setText(messages.get("libro.estado"));
    }

    private void cargarLibrosDesdeApi() {
        System.out.println("Cargando libros desde la API...");
        String jsonLibros = apiClient.fetchAllLibros();
        if (jsonLibros != null) {
            System.out.println("Libros recibidos: " + jsonLibros);
            Gson gson = new Gson();
            LibroDTO[] librosArray = gson.fromJson(jsonLibros, LibroDTO[].class);
            libros.clear();
            for (LibroDTO libroDTO : librosArray) {
                if (libroDTO != null && libroDTO.getIsbn() != null) {
                    Libro libro = new Libro(
                            libroDTO.getIsbn(),
                            libroDTO.getTitulo(),
                            libroDTO.getAutor(),
                            libroDTO.getCategoria(),
                            libroDTO.getEstado()
                    );
                    libros.add(libro);
                } else {
                    System.out.println("LibroDTO nulo o sin ISBN: " + libroDTO);
                }
            }
        } else {
            System.out.println("Error al obtener los libros de la API");
            showAlert(messages.get("alert.error"), "No se pudo cargar la lista de libros. Asegúrate de que la API esté ejecutándose.");
        }
    }

    private void buscarLibros() {
        System.out.println("Buscando libros con ISBN: " + isbnField.getText());
    }

    private void recargarLibros() {
        cargarLibrosDesdeApi();
    }

    private void openPantallaCrearLibro() {
        PantallaCrearLibro pantallaCrearLibro = new PantallaCrearLibro(libro -> {
            libros.add(libro);
            cargarLibrosDesdeApi(); // Recargar la lista después de añadir un libro
        }, messages);
        try {
            pantallaCrearLibro.show();
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error al abrir la pantalla de crear libro");
            showAlert(messages.get("alert.error"), "Error al abrir la pantalla de crear libro");
        }
    }

    private void eliminarLibro() {
        Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (libroSeleccionado != null) {
            boolean eliminado = apiClient.eliminarLibroPorIsbn(libroSeleccionado.getIsbn());
            if (eliminado) {
                libros.remove(libroSeleccionado);
                cargarLibrosDesdeApi(); // Actualizar la tabla después de eliminar un libro
            } else {
                showAlert(messages.get("alert.error"), "No se pudo eliminar el libro");
            }
        } else {
            showAlert(messages.get("alert.error"), messages.get("alert.no.seleccionado"));
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