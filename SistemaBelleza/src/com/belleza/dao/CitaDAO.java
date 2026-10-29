package com.belleza.dao;

import com.belleza.modelo.Cita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    public List<Cita> cargarCitas() throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT id_cita, cliente, empleado, servicio, precio_servicio, fecha, hora, estado FROM cita";
        try (Connection c = ConexionBD.obtener();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Cita cita = new Cita(
                        rs.getInt("id_cita"),
                        rs.getString("cliente"),
                        rs.getString("empleado"),
                        rs.getString("servicio"),
                        rs.getDouble("precio_servicio"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime()
                );
                cita.setEstado(rs.getString("estado"));
                lista.add(cita);
            }
        }
        return lista;
    }

    public void guardarCita(Cita cita) throws SQLException {
        String sql = "INSERT INTO cita (cliente, empleado, servicio, precio_servicio, fecha, hora, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cita.getCliente());
            ps.setString(2, cita.getEmpleado());
            ps.setString(3, cita.getServicio());
            ps.setDouble(4, cita.getPrecioServicio());
            ps.setDate(5, Date.valueOf(cita.getFecha()));
            ps.setTime(6, Time.valueOf(cita.getHora()));
            ps.setString(7, cita.getEstado());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    cita.setIdCita(keys.getInt(1));
                }
            }
        }
    }

    public void eliminarCita(int idCita) throws SQLException {
        String sql = "DELETE FROM cita WHERE id_cita = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            ps.executeUpdate();
        }
    }
}