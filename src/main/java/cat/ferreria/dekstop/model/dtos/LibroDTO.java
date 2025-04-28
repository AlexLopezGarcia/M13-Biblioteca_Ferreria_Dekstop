package cat.ferreria.dekstop.model.dtos;

public class LibroDTO {
    private long libro_id;
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private String estado;

    // Constructor para creación de LibroDTO
    public LibroDTO(long libro_id, String isbn, String titulo, String autor, String categoria, String estado) {
        this.libro_id = libro_id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.estado = estado;
    }

    // Getters
    public long getLibro_id() { return libro_id; }
    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getCategoria() { return categoria; }
    public String getEstado() { return estado; }
}