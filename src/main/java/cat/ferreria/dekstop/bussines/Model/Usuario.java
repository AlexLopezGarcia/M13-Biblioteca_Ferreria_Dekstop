package cat.ferreria.dekstop.bussines.Model;

public class Usuario {
    private String dni;
    private String nombre;
    private String contrasena;
    private String correoElectronico;

    public Usuario(String dni, String nombre, String contrasena, String correoElectronico) {
        this.dni = dni;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.correoElectronico = correoElectronico;
    }

    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getContrasena() { return contrasena; }
    public String getCorreoElectronico() { return correoElectronico; }

    public void setDni(String dni) { this.dni = dni; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
}


