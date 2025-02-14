package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Libro {

    private String apiUrl = "http://localhost:9090/libros";

    URL url = new URL(apiUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET"); //Método GET
    conn.setRequestProperty("Accept", "application/json");

    if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Error HTTP: " + conn.getResponseCode());
    }

    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
    String output;
    StringBuilder response = new StringBuilder();
    while ((output = br.readLine()) != null) {
        response.append(output);
    }
    conn.disconnect();
    System.out.println("Respuesta de la API: " + response.toString());

    private final StringProperty isbn;
    private final StringProperty titulo;
    private final StringProperty autor;
    private final StringProperty editorial;
    private final StringProperty categoria;
    private final StringProperty estado;

    public Libro(String isbn, String titulo, String autor, String editorial, String categoria, String estado) {
        this.isbn = new SimpleStringProperty(isbn);
        this.titulo = new SimpleStringProperty(titulo);
        this.autor = new SimpleStringProperty(autor);
        this.editorial = new SimpleStringProperty(editorial);
        this.categoria = new SimpleStringProperty(categoria);
        this.estado = new SimpleStringProperty(estado);
    }



    public StringProperty isbnProperty() { return isbn; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty autorProperty() { return autor; }
    public StringProperty editorialProperty() { return editorial; }
    public StringProperty categoriaProperty() { return categoria; }
    public StringProperty estadoProperty() { return estado; }
}
