package cat.ferreria.dekstop.model.dtos;

public class UsuarioDTO {
    private String dni;
    private String nombre;
    private String contrasena;
    private String correoElectronico;

    public UsuarioDTO(String dni, String nombre, String correoElectronico) {
        this.dni = dni;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.correoElectronico = correoElectronico;
    }

    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getContrasena() { return contrasena; }
    public String getCorreoElectronico() { return correoElectronico; }
}

