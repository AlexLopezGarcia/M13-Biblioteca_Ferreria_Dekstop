package cat.ferreria.dekstop.model.clazz;

import javafx.scene.image.Image;

public class Idioma {
    private final String codigo;
    private final String nombre;
    private final Image icono;

    public Idioma(String codigo, String nombre, Image icono) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.icono = icono;
    }

    public String getCodigo() { return codigo; }

    public String getNombre() { return nombre; }

    public Image getIcono() { return icono; }

    @Override
    public String toString() {
        return nombre;
    }
}
