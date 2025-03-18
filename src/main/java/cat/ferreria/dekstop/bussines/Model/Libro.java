package cat.ferreria.dekstop.bussines.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

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

    public String getIsbn() { return isbn.get(); }
    public String getTitulo() { return titulo.get(); }
    public void setTitulo(String titulo) { this.titulo.set(titulo); }
    public String getAutor() { return autor.get(); }
    public void setAutor(String autor) { this.autor.set(autor); }
    public String getCategoria() { return categoria.get(); }
    public void setCategoria(String categoria) { this.categoria.set(categoria); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }

    public StringProperty isbnProperty() { return isbn; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty autorProperty() { return autor; }
    public StringProperty categoriaProperty() { return categoria; }
    public StringProperty estadoProperty() { return estado; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Libro libro = (Libro) obj;
        return isbn.equals(libro.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

}
