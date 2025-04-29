package cat.ferreria.dekstop.model.clazz;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Libro {
    private final LongProperty libroId = new SimpleLongProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty titulo = new SimpleStringProperty();
    private final StringProperty autor = new SimpleStringProperty();
    private final StringProperty categoria = new SimpleStringProperty();
    private final StringProperty estado = new SimpleStringProperty();

    public Libro(Long libroId, String isbn, String titulo, String autor, String categoria, String estado) {
        this.libroId.set(libroId != null ? libroId : 0L);
        this.isbn.set(isbn);
        this.titulo.set(titulo);
        this.autor.set(autor);
        this.categoria.set(categoria);
        this.estado.set(estado);
    }

    public Long getLibroId() {
        return libroId.get();
    }

    public LongProperty libroIdProperty() {
        return libroId;
    }

    public String getIsbn() {
        return isbn.get();
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public String getTitulo() {
        return titulo.get();
    }

    public StringProperty tituloProperty() {
        return titulo;
    }

    public String getAutor() {
        return autor.get();
    }

    public StringProperty autorProperty() {
        return autor;
    }

    public String getCategoria() {
        return categoria.get();
    }

    public StringProperty categoriaProperty() {
        return categoria;
    }

    public String getEstado() {
        return estado.get();
    }

    public StringProperty estadoProperty() {
        return estado;
    }
}