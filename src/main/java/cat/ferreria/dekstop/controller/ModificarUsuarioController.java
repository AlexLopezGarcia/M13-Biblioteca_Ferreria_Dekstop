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
                        messages.get("alert.nada.seleccionado.titulo"),
                        messages.get("alert.nada.seleccionado.cabecera"),
                        messages.get("alert.nada.seleccionado.contenido"));
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle(messages.get("alert.confirmar.eliminacion.titulo"));
            confirmacion.setHeaderText(messages.get("alert.confirmar.eliminacion.cabecera"));
            confirmacion.setContentText(messages.get("alert.confirmar.eliminacion.contenido"));

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean error = false;
                    for (Usuario usuario : seleccionados) {
                        boolean eliminado = apiClient.eliminarUsuarioEnAPI(usuario.getDni());
                        if (!eliminado) {
                            error = true;
                        }
                    }

                    if (error) {
                        mostrarAlerta(Alert.AlertType.ERROR,
                                messages.get("alert.no.modificado.titulo"),
                                messages.get("alert.no.modificado.cabecera"),
                                messages.get("alert.no.modificado.contenido"));
                    } else {
                        mostrarAlerta(Alert.AlertType.INFORMATION,
                                messages.get("alert.exito.eliminacion"),
                                messages.get("alert.exito.eliminacion"),
                                messages.get("alert.exito.eliminacion"));
                        cargarUsuariosDesdeApi();
                    }
                }
            });
        });
    }


    private void mostrarVentanaEdicion(Usuario usuario) {
        Dialog<UsuarioDTO> dialog = new Dialog<>();
        dialog.setTitle(messages.get("button.editar.usuario"));

        TextField tfDni = new TextField(usuario.getDni());
        TextField tfNombre = new TextField(usuario.getNombre());
        TextField tfCorreo = new TextField(usuario.getCorreoElectronico());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.addRow(0, new Label(messages.get("label.dni")), tfDni);
        grid.addRow(1, new Label(messages.get("label.nombre")), tfNombre);
        grid.addRow(2, new Label(messages.get("label.correo")), tfCorreo);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
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
                if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", correo)) {
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
            Usuario usuarioModificado = new Usuario(
                    usuarioDTO.getDni(),
                    usuarioDTO.getNombre(),
                    usuario.getContrasena(),  // conservar contraseña
                    usuarioDTO.getCorreoElectronico()
            );

            boolean modificado = apiClient.modificarUsuarioEnAPI(usuario.getDni(), usuarioModificado);

            if (modificado) {
                mostrarAlerta(Alert.AlertType.INFORMATION,
                        messages.get("alert.modificado.titulo"),
                        messages.get("alert.modificado.cabecera"),
                        messages.get("alert.modificado.contenido"));
                cargarUsuariosDesdeApi();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR,
                        messages.get("alert.no.modificado.titulo"),
                        messages.get("alert.no.modificado.cabecera"),
                        messages.get("alert.no.modificado.contenido"));
            }
        });
    }

    private void cargarUsuariosDesdeApi() {
        String jsonUsuarios = apiClient.fetchUsuarios();

        if (jsonUsuarios == null || jsonUsuarios.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Error", "Respuesta vacía", "No se recibieron datos de usuarios.");
            return;
        }

        if (!jsonUsuarios.trim().startsWith("[")) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error", "Formato inesperado",
                    "La respuesta del servidor no es una lista de usuarios.\nContenido:\n" + jsonUsuarios);
            return;
        }

        try {
            Gson gson = new Gson();
            UsuarioDTO[] usuariosArray = gson.fromJson(jsonUsuarios, UsuarioDTO[].class);

            usuarios.clear();
            for (UsuarioDTO dto : usuariosArray) {
                usuarios.add(new Usuario(dto.getDni(), dto.getNombre(), dto.getContrasena(), dto.getCorreoElectronico()));
            }

            tablaUsuarios.setItems(usuarios); // restablecer tabla a todos los usuarios

        } catch (Exception e) {
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