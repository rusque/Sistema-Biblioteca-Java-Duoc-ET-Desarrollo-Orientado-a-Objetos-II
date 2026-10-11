package cl.duoc.biblioteca.dao;

import java.util.List;

// Interfaz genérica para estandarizar los métodos CRUD
public interface ICrud<T> {
    boolean crear(T objeto);
    List<T> leerTodos();
    boolean actualizar(T objeto);
    boolean eliminar(int id);
}