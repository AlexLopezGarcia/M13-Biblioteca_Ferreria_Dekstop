package cat.ferreria.dekstop.model.clazz;

import java.time.LocalDate;

public class Prestamo {
    private int id;
    private int idLibro;
    private String usuario;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private boolean devuelto;

    public Prestamo(int id, int idLibro, String usuario, LocalDate fechaPrestamo, LocalDate fechaLimite, boolean devuelto) {
        this.id = id;
        this.idLibro = idLibro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.devuelto = devuelto;
    }

    public int getId() {
        return id;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public String getUsuario() {
        return usuario;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }
}

