package cat.ferreria.dekstop.model.clazz;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Libro {
    private final StringProperty isbn;
    private final StringProperty titulo;
    private final StringProperty autor;
    private final StringProperty categoria;
    private final StringProperty estado;

    public Libro(String isbn, String titulo, String autor, String categoria, String estado) {
        this.isbn = new SimpleStringProperty(isbn);
        this.titulo = new SimpleStringProperty(titulo);
        this.autor = new SimpleStringProperty(autor);
        this.categoria = new SimpleStringProperty(categoria);
        this.estado = new SimpleStringProperty(estado);
    }

    public StringProperty isbnProperty() { return isbn; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty autorProperty() { return autor; }
    public StringProperty categoriaProperty() { return categoria; }
    public StringProperty estadoProperty() { return estado; }
}
