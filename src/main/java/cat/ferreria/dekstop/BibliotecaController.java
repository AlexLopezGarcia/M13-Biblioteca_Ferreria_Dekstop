package cat.ferreria.dekstop;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class BibliotecaController {
    @FXML private TextField isbnField;
    @FXML private TextField tituloField;
    @FXML private TextField autorField;
    @FXML private TextField editorialField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private Button buscarButton;
    @FXML private Button btnAñadirLibro;
    @FXML private Button btnModificarLibro;

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, String> colISBN;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colEditorial;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, String> colEstado;

    private ObservableList<Libro> libros = FXCollections.observableArrayList();
    private ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        categoriaComboBox.setItems(FXCollections.observableArrayList(
                "Narrativa", "Novela juvenil", "Bibliografía"
        ));

        colISBN.setCellValueFactory(data -> data.getValue().isbnProperty());
        colTitulo.setCellValueFactory(data -> data.getValue().tituloProperty());
        colAutor.setCellValueFactory(data -> data.getValue().autorProperty());
        colEditorial.setCellValueFactory(data -> data.getValue().editorialProperty());
        colCategoria.setCellValueFactory(data -> data.getValue().categoriaProperty());
        colEstado.setCellValueFactory(data -> data.getValue().estadoProperty());

        tablaLibros.setItems(libros);

        cargarLibrosDesdeApi();

        buscarButton.setOnAction(event -> buscarLibros());
        btnAñadirLibro.setOnAction(event -> openPantallaCrearLibro());
        btnModificarLibro.setOnAction(actionEvent -> openPantallaEditarLibro());
    }

    private void cargarLibrosDesdeApi() {
        String jsonHistorial = apiClient.fetchHistorial();
        if (jsonHistorial != null) {
            Gson gson = new Gson();
            HistorialDTO[] historialArray = gson.fromJson(jsonHistorial, HistorialDTO[].class);
            libros.clear();
            for (HistorialDTO historialDTO : historialArray) {
                String jsonLibro = apiClient.fetchLibroByIsbn(historialDTO.getIsbn());
                if (jsonLibro != null) {
                    LibroDTO libroDTO = gson.fromJson(jsonLibro, LibroDTO.class);
                    Libro libro = new Libro(
                            libroDTO.getIsbn(),
                            libroDTO.getTitulo(),
                            libroDTO.getAutor(),
                            libroDTO.getEditorial(),
                            libroDTO.getCategoria(),
                            libroDTO.getEstado()
                    );
                    libros.add(libro);
                }
            }
        } else {
            System.out.println("Error al obtener el historial de la API");
        }
    }

    private void buscarLibros() {
        System.out.println("Buscando libros con ISBN: " + isbnField.getText());
    }

    private void openPantallaCrearLibro() {
        PantallaCrearLibro pantallaCrearLibro = new PantallaCrearLibro(libro -> {
            libros.add(libro);
        });
        try {
            pantallaCrearLibro.show();
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error al abrir la pantalla de crear libro");
        }
    }

    private void openPantallaEditarLibro() {
        PantallaModificarLibro pantallaModificarLibro = new PantallaModificarLibro(libro -> {
            libros.set(libros.indexOf(libro), libro);
        });
        try {
            pantallaModificarLibro.show();
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error al abrir la pantalla de editar libro");
        }
    }
}