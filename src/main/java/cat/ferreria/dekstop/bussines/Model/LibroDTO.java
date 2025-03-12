package cat.ferreria.dekstop.bussines.Model;

public class LibroDTO {
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private String estado;

    // Constructor para creación de LibroDTO
    public LibroDTO(String isbn, String titulo, String autor, String editorial ,String categoria, String estado) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.categoria = categoria;
        this.estado = estado;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getCategoria() { return categoria; }
    public String getEstado() { return estado; }
}