package cat.ferreria.dekstop.model.dtos;

import com.google.gson.annotations.SerializedName;

/**
 * Clase DTO para libro para utilizar en la comunicación con el servidor
 *
 * @author alexl
 * @date 01/10/2024
 * */

public class LibroDTO {
    @SerializedName("libroId")
    private Long libroId;
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private String editorial;
    @SerializedName("estadoUso")
    private boolean estadoUso;

    // Getters y setters
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

    public String getEditorial() {
        return editorial;
    }
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getEstado() {
        return estadoUso ? "Disponible" : "Prestado";
    }
    public void setEstado(String estado) {
        this.estadoUso = "Disponible".equals(estado);
    }

    public boolean getEstadoUso() {
        return estadoUso;
    }
    public void setEstadoUso(boolean estadoUso) {
        this.estadoUso = estadoUso;
    }
}
