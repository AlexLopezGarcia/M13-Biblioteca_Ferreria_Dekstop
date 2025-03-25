package cat.ferreria.dekstop.Controller;

import cat.ferreria.dekstop.ApiClient;
import cat.ferreria.dekstop.bussines.Model.HistorialDTO;
import cat.ferreria.dekstop.bussines.Model.Libro;
import cat.ferreria.dekstop.bussines.Model.LibroDTO;
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
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class BibliotecaController {
    @FXML private TextField isbnField;
    @FXML private TextField tituloField;
    @FXML private TextField autorField;
    @FXML private TextField editorialField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private Button buscarButton;
    @FXML private ComboBox<String> idiomaComboBox;  // ComboBox para los idiomas

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
        try {
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
            configurarComboBoxIdiomas();

            buscarButton.setOnAction(event -> buscarLibros());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error en initialize(): " + e.getMessage());
        }
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

    private void configurarComboBoxIdiomas() {
        // Lista de idiomas
        ObservableList<String> idiomas = FXCollections.observableArrayList("Español", "Catalán", "Inglés");
        idiomaComboBox.setItems(idiomas);

        // Personalizar celdas con imágenes
        idiomaComboBox.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Text text = new Text();
            private final HBox hbox = new HBox(10, imageView, text);

            {
                imageView.setFitWidth(24);
                imageView.setFitHeight(16);
            }

            @Override
            protected void updateItem(String idioma, boolean empty) {
                super.updateItem(idioma, empty);

                if (empty || idioma == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(getIconForLanguage(idioma));
                    text.setText(idioma);
                    setGraphic(hbox);
                }
            }
        });

        // Mostrar icono en la celda seleccionada
        idiomaComboBox.setButtonCell(new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Text text = new Text();
            private final HBox hbox = new HBox(10, imageView, text);

            {
                imageView.setFitWidth(24);
                imageView.setFitHeight(16);
            }

            @Override
            protected void updateItem(String idioma, boolean empty) {
                super.updateItem(idioma, empty);

                if (empty || idioma == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(getIconForLanguage(idioma));
                    text.setText(idioma);
                    setGraphic(hbox);
                }
            }
        });
    }

    private Image getIconForLanguage(String idioma) {
        return switch (idioma) {
            case "Español" -> new Image(getClass().getResourceAsStream("/img/espana.png"));
            case "Catalán" -> new Image(getClass().getResourceAsStream("/img/catalunya.png"));
            case "Inglés" -> new Image(getClass().getResourceAsStream("/img/reinoUnido.png"));
            default -> null;
        };
    }

    @FXML
    private void abrirRegistroUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registrar_usuario.fxml"));
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

    private void buscarLibros() {
        System.out.println("Buscando libros con ISBN: " + isbnField.getText());
    }
}
