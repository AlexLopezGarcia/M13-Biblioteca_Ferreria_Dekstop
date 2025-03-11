package cat.ferreria.dekstop;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario {
    private final StringProperty dni;
    private final StringProperty nombre;
    private final StringProperty contrasena;
    private final StringProperty correoElectronico;

    public Usuario(String dni, String nombre, String contrasena, String correoElectronico) {
        this.dni = new SimpleStringProperty(dni);
        this.nombre = new SimpleStringProperty(nombre);
        this.contrasena = new SimpleStringProperty(contrasena);
        this.correoElectronico = new SimpleStringProperty(correoElectronico);
    }

    public StringProperty dniProperty() { return dni; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty contrasenaProperty() { return contrasena; }
    public StringProperty correoElectronicoProperty() { return correoElectronico; }

    public String getDni() { return dni.get(); }
    public String getNombre() { return nombre.get(); }
    public String getContrasena() { return contrasena.get(); }
    public String getCorreoElectronico() { return correoElectronico.get(); }

    public void setDni(String dni) { this.dni.set(dni); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setContrasena(String contrasena) { this.contrasena.set(contrasena); }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico.set(correoElectronico); }
}

