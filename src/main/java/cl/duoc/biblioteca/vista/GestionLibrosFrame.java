package cl.duoc.biblioteca.vista;

import cl.duoc.biblioteca.dao.LibroDAO;
import cl.duoc.biblioteca.modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionLibrosFrame extends JFrame {

    private JTextField txtId, txtTitulo, txtAutor, txtIsbn, txtEditorial, txtStock, txtCategoria;
    private JButton btnAgregar, btnEditar, btnEliminar, btnLimpiar;
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;
    private LibroDAO libroDAO;

    public GestionLibrosFrame() {
        libroDAO = new LibroDAO();

        setTitle("Gestión de Libros");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Superior: Formulario de datos ---
        JPanel panelFormulario = new JPanel(new GridLayout(4, 4, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        panelFormulario.add(new JLabel("ID (Solo lectura):"));
        txtId = new JTextField();
        txtId.setEditable(false); // No se debe editar a mano
        panelFormulario.add(txtId);

        panelFormulario.add(new JLabel("Título:"));
        txtTitulo = new JTextField();
        panelFormulario.add(txtTitulo);

        panelFormulario.add(new JLabel("Autor:"));
        txtAutor = new JTextField();
        panelFormulario.add(txtAutor);

        panelFormulario.add(new JLabel("ISBN:"));
        txtIsbn = new JTextField();
        panelFormulario.add(txtIsbn);

        panelFormulario.add(new JLabel("Editorial:"));
        txtEditorial = new JTextField();
        panelFormulario.add(txtEditorial);

        panelFormulario.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        panelFormulario.add(txtStock);

        panelFormulario.add(new JLabel("ID Categoría (1,2 o 3):"));
        txtCategoria = new JTextField();
        panelFormulario.add(txtCategoria);

        add(panelFormulario, BorderLayout.NORTH);

        // --- Panel Central: Tabla para listar ---
        String[] columnas = {"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "ID Categoría"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evitar que el usuario escriba directamente en las celdas
            }
        };
        tablaLibros = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaLibros);
        add(scrollPane, BorderLayout.CENTER);

        // --- Panel Inferior: Botones de Acción ---
        JPanel panelBotones = new JPanel();
        btnAgregar = new JButton("Agregar Libro");
        btnEditar = new JButton("Actualizar Seleccionado");
        btnEliminar = new JButton("Eliminar Seleccionado");
        btnLimpiar = new JButton("Limpiar Campos");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        add(panelBotones, BorderLayout.SOUTH);

        // --- Eventos de los Botones ---
        btnAgregar.addActionListener(e -> agregarLibro());
        btnEditar.addActionListener(e -> actualizarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        // Evento: Al hacer clic en una fila de la tabla, cargar datos en el formulario
        tablaLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaLibros.getSelectedRow() != -1) {
                llenarFormularioDesdeTabla();
            }
        });

        // Cargar los libros desde la Base de Datos al abrir la ventana
        cargarTabla();
    }

    // --- MÉTODOS DE LÓGICA ---

    private void cargarTabla() {
        modeloTabla.setRowCount(0); // Limpiar la tabla visualmente
        List<Libro> libros = libroDAO.leerTodos(); // Traer de la BD

        for (Libro lib : libros) {
            Object[] fila = {
                    lib.getId(),
                    lib.getTitulo(),
                    lib.getAutor(),
                    lib.getIsbn(),
                    lib.getEditorial(),
                    lib.getStock(),
                    lib.getIdCategoria()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void llenarFormularioDesdeTabla() {
        int fila = tablaLibros.getSelectedRow();
        txtId.setText(tablaLibros.getValueAt(fila, 0).toString());
        txtTitulo.setText(tablaLibros.getValueAt(fila, 1).toString());
        txtAutor.setText(tablaLibros.getValueAt(fila, 2).toString());
        txtIsbn.setText(tablaLibros.getValueAt(fila, 3).toString());
        txtEditorial.setText(tablaLibros.getValueAt(fila, 4).toString());
        txtStock.setText(tablaLibros.getValueAt(fila, 5).toString());
        txtCategoria.setText(tablaLibros.getValueAt(fila, 6).toString());
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        txtStock.setText("");
        txtCategoria.setText("");
        tablaLibros.clearSelection();
    }

    private void agregarLibro() {
        try {
            Libro libro = new Libro();
            libro.setTitulo(txtTitulo.getText());
            libro.setAutor(txtAutor.getText());
            libro.setIsbn(txtIsbn.getText());
            libro.setEditorial(txtEditorial.getText());
            libro.setStock(Integer.parseInt(txtStock.getText()));
            libro.setIdCategoria(Integer.parseInt(txtCategoria.getText()));

            if (libroDAO.crear(libro)) {
                JOptionPane.showMessageDialog(this, "¡Libro agregado correctamente!");
                cargarTabla(); // Refrescar la tabla
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar el libro en la Base de Datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Asegúrese de que el Stock y el ID Categoría sean números válidos.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarLibro() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero seleccione un libro de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Libro libro = new Libro();
            libro.setId(Integer.parseInt(txtId.getText()));
            libro.setTitulo(txtTitulo.getText());
            libro.setAutor(txtAutor.getText());
            libro.setIsbn(txtIsbn.getText());
            libro.setEditorial(txtEditorial.getText());
            libro.setStock(Integer.parseInt(txtStock.getText()));
            libro.setIdCategoria(Integer.parseInt(txtCategoria.getText()));

            if (libroDAO.actualizar(libro)) {
                JOptionPane.showMessageDialog(this, "¡Libro actualizado correctamente!");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stock y Categoría deben ser números.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarLibro() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este libro de la base de datos?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            if (libroDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Libro eliminado.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar. Verifique que el libro no tenga préstamos activos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}