package cat.ferreria.dekstop.model.clazz;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Entidad d'historial para la trata de información
 *
 * @author alexl
 * @date 13/02/2025
 * */

public class Historial {
    private final StringProperty historialId;
    private final StringProperty dni;
    private final StringProperty isbn;

    public Historial(String historialId, String dni, String isbn) {
        this.historialId = new SimpleStringProperty(historialId);
        this.dni = new SimpleStringProperty(dni);
        this.isbn = new SimpleStringProperty(isbn);
    }

    public StringProperty historialIdProperty() { return historialId; }
    public StringProperty dniProperty() { return dni; }
    public StringProperty isbnProperty() { return isbn; }

    public String getIsbn() { return isbn.get(); }
}