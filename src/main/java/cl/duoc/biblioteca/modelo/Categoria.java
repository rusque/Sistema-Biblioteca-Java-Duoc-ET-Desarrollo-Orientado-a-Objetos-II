package cl.duoc.biblioteca.modelo;

public class Categoria {
    private int id;
    private String nombre;

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Categoria() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Útil para que los ComboBox en el JFrame muestren el nombre de la categoría
    @Override
    public String toString() {
        return nombre;
    }
}