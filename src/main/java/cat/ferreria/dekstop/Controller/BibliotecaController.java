package cat.ferreria.dekstop.Controller;

import cat.ferreria.dekstop.*;
import cat.ferreria.dekstop.bussines.Model.HistorialDTO;
import cat.ferreria.dekstop.bussines.Model.Libro;
import cat.ferreria.dekstop.bussines.Model.LibroDTO;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static java.util.Arrays.stream;

public class BibliotecaController {
    @FXML private TextField isbnField;
    @FXML private TextField tituloField;
    @FXML private TextField autorField;
    @FXML private TextField editorialField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private Button buscarButton;
    @FXML private Button btnAnyadir;
    @FXML private Button btnModificar;

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
        colCategoria.setCellValueFactory(data -> data.getValue().categoriaProperty());
        colEstado.setCellValueFactory(data -> data.getValue().estadoProperty());

        tablaLibros.setItems(libros);

        cargarLibrosDesdeApi();

        btnAnyadir.setOnAction(event -> openPantallaCrearLibro());
        btnModificar.setOnAction(event -> openPantallaModificarLibro());
        buscarButton.setOnAction(event -> buscarLibros());
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

    public void openPantallaModificarLibro() {
        Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            showAlert("Error", "Debes seleccionar un libro de la tabla");
            return;
        }

        PantallaModificarLibro pantallaModificarLibro = new PantallaModificarLibro(libroSeleccionado, libroModificado -> {
            int index = libros.indexOf(libroSeleccionado);
            if (index != -1) {
                libros.set(index, libroModificado);
                tablaLibros.refresh();
            } else {
                showAlert("Error", "El libro seleccionado ya no está disponible en la lista");
            }
        });

        try {
            pantallaModificarLibro.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo abrir la pantalla de modificar libro");
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