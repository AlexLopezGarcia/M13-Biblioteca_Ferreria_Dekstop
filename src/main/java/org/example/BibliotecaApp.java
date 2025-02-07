package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BibliotecaApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Barra superior con botones
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: rgba(65,129,1,1);");

        Button btnDevolucion = new Button("Registrar Devolución");
        Button btnPrestamo = new Button("Registrar Préstamo");
        Button btnEliminar = new Button("Eliminar Libro");
        Button btnModificar = new Button("Modificar Libro");
        Button btnAñadir = new Button("Añadir Libro");
        Button btnLogin = new Button("Logarse");
        Button btnRegistrarUsuario = new Button("Registrar Usuario");

        btnDevolucion.setStyle("-fx-background-color: rgba(85,204,1,1); -fx-text-fill: white;");
        btnPrestamo.setStyle("-fx-background-color: rgba(85,204,1,1); -fx-text-fill: white;");
        btnEliminar.setStyle("-fx-background-color: rgba(85,204,1,1); -fx-text-fill: white;");
        btnModificar.setStyle("-fx-background-color: rgba(85,204,1,1); -fx-text-fill: white;");
        btnAñadir.setStyle("-fx-background-color: rgba(85,204,1,1); -fx-text-fill: white;");
        btnLogin.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
        btnRegistrarUsuario.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        topBar.getChildren().addAll(btnDevolucion, btnPrestamo, btnEliminar, btnModificar, btnAñadir, btnLogin, btnRegistrarUsuario);

        // Campos de búsqueda
        GridPane searchPane = new GridPane();
        searchPane.setPadding(new Insets(20));
        searchPane.setHgap(10);
        searchPane.setVgap(10);

        TextField txtISBN = new TextField();
        TextField txtTitulo = new TextField();
        TextField txtAutor = new TextField();
        TextField txtEditorial = new TextField();
        ComboBox<String> cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll("Narrativa", "Novela juvenil", "Bibliografía");
        Button btnBuscar = new Button("Buscar");

        searchPane.add(new Label("ISBN"), 0, 0);
        searchPane.add(txtISBN, 1, 0);
        searchPane.add(new Label("Título"), 2, 0);
        searchPane.add(txtTitulo, 3, 0);
        searchPane.add(new Label("Autor"), 4, 0);
        searchPane.add(txtAutor, 5, 0);
        searchPane.add(new Label("Editorial"), 0, 1);
        searchPane.add(txtEditorial, 1, 1);
        searchPane.add(new Label("Categoría"), 2, 1);
        searchPane.add(cbCategoria, 3, 1);
        searchPane.add(btnBuscar, 5, 1);

        // Tabla de libros
        TableView<String> table = new TableView<>();
        TableColumn<String, String> colISBN = new TableColumn<>("ISBN");
        TableColumn<String, String> colTitulo = new TableColumn<>("Título");
        TableColumn<String, String> colAutor = new TableColumn<>("Autor");
        TableColumn<String, String> colEditorial = new TableColumn<>("Editorial");
        TableColumn<String, String> colCategoria = new TableColumn<>("Categoría");
        TableColumn<String, String> colEstado = new TableColumn<>("Estado De Uso");

        table.getColumns().addAll(colISBN, colTitulo, colAutor, colEditorial, colCategoria, colEstado);

        VBox root = new VBox(10, topBar, searchPane, table);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Biblioteca");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
