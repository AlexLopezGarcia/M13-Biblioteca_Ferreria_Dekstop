package cat.ferreria.dekstop.model.dtos;

/**
 * Clase DTO para historial para utilizar en la comunicación con el servidor.
 *
 * @author alexl
 * @date 01/10/2024
 * */

public class HistorialDTO {
    private int historialId;
    private String dni;
    private String libroId; // Cambiado de isbn a libroId para coincidir con el JSON
    private String fechaPrestamo;
    private String fechaDevolucion;

    // Getters y setters
    public int getHistorialId() {
        return historialId;
    }

    public void setHistorialId(int historialId) {
        this.historialId = historialId;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getLibroId() {
        return libroId;
    }

    public void setLibroId(String libroId) {
        this.libroId = libroId;
    }

    public String getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(String fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
}