package cat.ferreria.dekstop.model.clazz;

import javafx.beans.property.*;

public class Libro {
    private final SimpleLongProperty libro_id;
    private final StringProperty isbn;
    private final StringProperty titulo;
    private final StringProperty autor;
    private final StringProperty categoria;
    private final StringProperty estado;

    public Libro(long libro_id, String isbn, String titulo, String autor, String categoria, String estado) {
        this.libro_id = new SimpleLongProperty(libro_id);
        this.isbn = new SimpleStringProperty(isbn);
        this.titulo = new SimpleStringProperty(titulo);
        this.autor = new SimpleStringProperty(autor);
        this.categoria = new SimpleStringProperty(categoria);
        this.estado = new SimpleStringProperty(estado);
    }

    public long getLibro_id() {
        return libro_id.get();
    }
    public String getIsbn() {
        return isbn.get();
    }

    public String getTitulo() {
        return titulo.get();
    }

    public String getAutor() {
        return autor.get();
    }

    public String getCategoria() {
        return categoria.get();
    }

    public String getEstado() {
        return estado.get();
    }

    public SimpleLongProperty libro_idProperty() { return libro_id; }
    public StringProperty isbnProperty() { return isbn; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty autorProperty() { return autor; }
    public StringProperty categoriaProperty() { return categoria; }
    public StringProperty estadoProperty() { return estado; }
}
