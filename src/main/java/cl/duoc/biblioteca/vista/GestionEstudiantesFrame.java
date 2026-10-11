package cl.duoc.biblioteca.vista;

import cl.duoc.biblioteca.dao.EstudianteDAO;
import cl.duoc.biblioteca.modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionEstudiantesFrame extends JFrame {

    private JTextField txtId, txtNombre, txtRut, txtCurso, txtCorreo;
    private JButton btnAgregar, btnEditar, btnEliminar, btnLimpiar;
    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;
    private EstudianteDAO estudianteDAO;

    public GestionEstudiantesFrame() {
        estudianteDAO = new EstudianteDAO();

        setTitle("Gestión de Estudiantes");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Superior: Formulario ---
        JPanel panelFormulario = new JPanel(new GridLayout(3, 4, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        panelFormulario.add(new JLabel("ID (Solo lectura):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelFormulario.add(txtId);

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("RUT:"));
        txtRut = new JTextField();
        panelFormulario.add(txtRut);

        panelFormulario.add(new JLabel("Curso:"));
        txtCurso = new JTextField();
        panelFormulario.add(txtCurso);

        panelFormulario.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        panelFormulario.add(txtCorreo);

        // Espacios vacíos para cuadrar la grilla
        panelFormulario.add(new JLabel(""));
        panelFormulario.add(new JLabel(""));

        add(panelFormulario, BorderLayout.NORTH);

        // --- Panel Central: Tabla ---
        String[] columnas = {"ID", "Nombre", "RUT", "Curso", "Correo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEstudiantes = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaEstudiantes);
        add(scrollPane, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel();
        btnAgregar = new JButton("Agregar Estudiante");
        btnEditar = new JButton("Actualizar Seleccionado");
        btnEliminar = new JButton("Eliminar Seleccionado");
        btnLimpiar = new JButton("Limpiar Campos");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        add(panelBotones, BorderLayout.SOUTH);

        // --- Eventos de los Botones ---
        btnAgregar.addActionListener(e -> agregarEstudiante());
        btnEditar.addActionListener(e -> actualizarEstudiante());
        btnEliminar.addActionListener(e -> eliminarEstudiante());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        // Evento: Al hacer clic en la tabla
        tablaEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaEstudiantes.getSelectedRow() != -1) {
                llenarFormularioDesdeTabla();
            }
        });

        cargarTabla();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Estudiante> estudiantes = estudianteDAO.leerTodos();

        for (Estudiante est : estudiantes) {
            Object[] fila = {
                    est.getId(), est.getNombre(), est.getRut(), est.getCurso(), est.getCorreo()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void llenarFormularioDesdeTabla() {
        int fila = tablaEstudiantes.getSelectedRow();
        txtId.setText(tablaEstudiantes.getValueAt(fila, 0).toString());
        txtNombre.setText(tablaEstudiantes.getValueAt(fila, 1).toString());
        txtRut.setText(tablaEstudiantes.getValueAt(fila, 2).toString());
        txtCurso.setText(tablaEstudiantes.getValueAt(fila, 3).toString());
        txtCorreo.setText(tablaEstudiantes.getValueAt(fila, 4).toString());
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        tablaEstudiantes.clearSelection();
    }

    private void agregarEstudiante() {
        if(txtNombre.getText().isEmpty() || txtRut.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Nombre y RUT son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Estudiante est = new Estudiante();
        est.setNombre(txtNombre.getText());
        est.setRut(txtRut.getText());
        est.setCurso(txtCurso.getText());
        est.setCorreo(txtCorreo.getText());

        if (estudianteDAO.crear(est)) {
            JOptionPane.showMessageDialog(this, "¡Estudiante agregado!");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstudiante() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Estudiante est = new Estudiante();
        est.setId(Integer.parseInt(txtId.getText()));
        est.setNombre(txtNombre.getText());
        est.setRut(txtRut.getText());
        est.setCurso(txtCurso.getText());
        est.setCorreo(txtCorreo.getText());

        if (estudianteDAO.actualizar(est)) {
            JOptionPane.showMessageDialog(this, "¡Estudiante actualizado!");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEstudiante() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Eliminar este estudiante?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            if (estudianteDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Estudiante eliminado.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar. Verifique que no tenga préstamos activos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}