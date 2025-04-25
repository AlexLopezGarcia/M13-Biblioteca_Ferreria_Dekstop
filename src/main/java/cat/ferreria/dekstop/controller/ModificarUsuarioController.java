package cat.ferreria.dekstop.controller;

import cat.ferreria.dekstop.dataaccess.ApiClient;
import cat.ferreria.dekstop.model.clazz.Usuario;
import cat.ferreria.dekstop.model.dtos.UsuarioDTO;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.Map;
import java.util.regex.Pattern;

public class ModificarUsuarioController {

    @FXML private TextField dniField;
    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private Button buscarButton;
    @FXML private CheckBox seleccionarTodosCheckBox;
    @FXML private Button btnEditarUsuario;
    @FXML private Button btnEliminarUsuario;
    @FXML private Label LabelDni;
    @FXML private Label LabelNombre;
    @FXML private Label LabelCorreo;

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colDni;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;

    private ApiClient apiClient = new ApiClient();
    private ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
    private Map<String, String> messages;

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
        updateUI(); // <-- Aquí actualizamos los textos visibles (botones, etc.)
    }

    private void updateUI() {
        if (messages == null) return;

        buscarButton.setText(messages.get("button.buscar"));
        btnEditarUsuario.setText(messages.get("button.editar.usuario"));
        btnEliminarUsuario.setText(messages.get("button.eliminar.usuario"));
        seleccionarTodosCheckBox.setText(messages.get("checkbox.seleccionar.todos"));

        LabelDni.setText(messages.get("label.dni"));
        colDni.setText(messages.get("label.columna.dni"));
        LabelNombre.setText(messages.get("label.nombre"));
        colNombre.setText(messages.get("label.columna.nombre"));
        LabelCorreo.setText(messages.get("label.correo"));
        colCorreo.setText(messages.get("label.columna.correo"));

    }
    @FXML
    public void initialize() {
        configurarTabla();
        cargarUsuariosDesdeApi();
        configurarBotones();

        buscarButton.setOnAction(event -> buscarUsuarios());

        seleccionarTodosCheckBox.setOnAction(event -> {
            if (seleccionarTodosCheckBox.isSelected()) {
                tablaUsuarios.getSelectionModel().selectAll();
            } else {
                tablaUsuarios.getSelectionModel().clearSelection();
            }
        });
    }


    private void configurarTabla() {
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        tablaUsuarios.setItems(usuarios);
        tablaUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }
    private void configurarBotones() {
        btnEditarUsuario.setOnAction(event -> {
            ObservableList<Usuario> seleccionados = tablaUsuarios.getSelectionModel().getSelectedItems();
            if (seleccionados.size() != 1) {
                mostrarAlerta(Alert.AlertType.WARNING,
                        messages.get("alert.seleccion.unica.titulo"),
                        messages.get("alert.seleccion.unica.cabecera"),
                        messages.get("alert.seleccion.unica.contenido"));

            } else {
                mostrarVentanaEdicion(seleccionados.get(0));
            }
        });

        btnEliminarUsuario.setOnAction(event -> {
            ObservableList<Usuario> seleccionados = tablaUsuarios.getSelectionModel().getSelectedItems();
            if (seleccionados.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING,
                        messages.get("alert.seleccion.unica.titulo"),
                        messages.get("alert.seleccion.unica.cabecera"),
                        messages.get("alert.seleccion.unica.contenido"));
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar los usuarios seleccionados?");
            confirmacion.setContentText("Esta acción no se puede deshacer.");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    for (Usuario usuario : seleccionados) {
                        apiClient.eliminarUsuarioEnAPI(usuario.getDni());
                    }
                    mostrarAlerta(Alert.AlertType.WARNING,
                            messages.get("alert.seleccion.unica.titulo"),
                            messages.get("alert.seleccion.unica.cabecera"),
                            messages.get("alert.seleccion.unica.contenido"));
                    cargarUsuariosDesdeApi();
                }
            });
        });
    }
    private void mostrarVentanaEdicion(Usuario usuario) {
        Dialog<UsuarioDTO> dialog = new Dialog<>();
        dialog.setTitle("Editar Usuario");

        Label labelDni = new Label("DNI:");
        TextField tfDni = new TextField(usuario.getDni());

        Label labelNombre = new Label("Nombre:");
        TextField tfNombre = new TextField(usuario.getNombre());

        Label labelCorreo = new Label("Correo:");
        TextField tfCorreo = new TextField(usuario.getCorreoElectronico());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(labelDni, 0, 0);
        grid.add(tfDni, 1, 0);
        grid.add(labelNombre, 0, 1);
        grid.add(tfNombre, 1, 1);
        grid.add(labelCorreo, 0, 2);
        grid.add(tfCorreo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String dni = tfDni.getText().trim();
                String nombre = tfNombre.getText().trim();
                String correo = tfCorreo.getText().trim();

                StringBuilder errores = new StringBuilder();

                if (dni.isEmpty() || nombre.isEmpty() || correo.isEmpty()) {
                    errores.append(messages.get("alert.validacion.vacia")).append("\n");
                }
                if (!dni.matches("^[0-9]{8}[A-Za-z]$")) {
                    errores.append(messages.get("alert.validacion.dni")).append("\n");
                }
                if (nombre.length() < 2 || nombre.length() > 30) {
                    errores.append(messages.get("alert.validacion.nombre")).append("\n");
                }
                String regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
                if (!Pattern.matches(regexCorreo, correo)) {
                    errores.append(messages.get("alert.validacion.correo")).append("\n");
                }


                if (errores.length() > 0) {
                    mostrarAlerta(Alert.AlertType.ERROR,
                            messages.get("alert.error"),
                            messages.get("alert.validacion.cabecera"),
                            errores.toString());
                    return null;
                }


                return new UsuarioDTO(dni, nombre, correo);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(usuarioDTO -> {
            boolean modificado = apiClient.modificarUsuarioEnAPI(usuario.getDni(), usuarioDTO);
            if (modificado) {
                mostrarAlerta(Alert.AlertType.WARNING,
                        messages.get("alert.seleccion.unica.titulo"),
                        messages.get("alert.seleccion.unica.cabecera"),
                        messages.get("alert.seleccion.unica.contenido"));
                cargarUsuariosDesdeApi();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING,
                        messages.get("alert.seleccion.unica.titulo"),
                        messages.get("alert.seleccion.unica.cabecera"),
                        messages.get("alert.seleccion.unica.contenido"));
            }
        });
    }


    private void cargarUsuariosDesdeApi() {
        String jsonUsuarios = apiClient.fetchUsuarios();
        if (jsonUsuarios != null) {
            Gson gson = new Gson();
            UsuarioDTO[] usuariosArray = gson.fromJson(jsonUsuarios, UsuarioDTO[].class);
            usuarios.clear();
            for (UsuarioDTO dto : usuariosArray) {
                usuarios.add(new Usuario(dto.getDni(), dto.getNombre(), dto.getContrasena(), dto.getCorreoElectronico()));
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING,
                    messages.get("alert.seleccion.unica.titulo"),
                    messages.get("alert.seleccion.unica.cabecera"),
                    messages.get("alert.seleccion.unica.contenido"));
        }
    }
    private void buscarUsuarios() {
        String dni = dniField.getText().trim().toLowerCase();
        String nombre = nombreField.getText().trim().toLowerCase();
        String correo = correoField.getText().trim().toLowerCase();

        ObservableList<Usuario> filtrados = FXCollections.observableArrayList();

        for (Usuario u : usuarios) {
            boolean coincide =
                    (!dni.isEmpty() && u.getDni().toLowerCase().contains(dni)) ||
                            (!nombre.isEmpty() && u.getNombre().toLowerCase().contains(nombre)) ||
                            (!correo.isEmpty() && u.getCorreoElectronico().toLowerCase().contains(correo));

            if (coincide) {
                filtrados.add(u);
            }
        }

        if (filtrados.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    messages.get("alert.resultado.vacio"), null, "");
        }


        tablaUsuarios.setItems(filtrados);
    }


    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    private void limpiarCampos() {
        dniField.setText("");
        nombreField.setText("");
        correoField.setText("");
    }
}
