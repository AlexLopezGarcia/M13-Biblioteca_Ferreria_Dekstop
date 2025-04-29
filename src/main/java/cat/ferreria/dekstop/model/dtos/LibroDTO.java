package cat.ferreria.dekstop.model.dtos;

import com.google.gson.annotations.SerializedName;

public class LibroDTO {
    @SerializedName("libroId")
    private Long libroId;
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    @SerializedName("estadoUso")
    private Boolean estadoUso;

    public LibroDTO() {}

    public LibroDTO(Long libroId, String isbn, String titulo, String autor, String categoria, String estado) {
        this.libroId = libroId;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.estadoUso = "Prestado".equals(estado);
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstado() {
        return estadoUso != null && estadoUso ? "Prestado" : "Disponible";
    }

    public void setEstado(String estado) {
        this.estadoUso = "Prestado".equals(estado);
    }
}