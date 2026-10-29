package com.belleza.dao;

import com.belleza.modelo.*;
import com.belleza.servicio.AuthService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> cargarUsuarios(AuthService auth) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, apellido, username, password, rol, estado FROM usuario";
        try (Connection c = ConexionBD.obtener();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_usuario");
                String nom = rs.getString("nombre");
                String ape = rs.getString("apellido");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String rol = rs.getString("rol");
                boolean estado = rs.getBoolean("estado");

                Usuario u;
                if ("ADMIN".equalsIgnoreCase(rol)) {
                    Administrador admin = new Administrador(id, nom, ape, user, pass);
                    admin.setAuthService(auth);
                    u = admin;
                } else {
                    Empleado emp = new Empleado(id, nom, ape, user, pass);
                    emp.setAuthService(auth);
                    u = emp;
                }
                u.setEstado(estado);
                lista.add(u);
            }
        }
        return lista;
    }

    public void insertarUsuario(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, apellido, username, password, rol, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getUsername());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRol());
            ps.setBoolean(6, u.isEstado());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setIdUsuario(keys.getInt(1));
                }
            }
        }
    }

    public void eliminarUsuarioBD(String username) throws SQLException {
        String sql = "DELETE FROM usuario WHERE username = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    public void actualizarEstadoBD(String username, boolean estado) throws SQLException {
        String sql = "UPDATE usuario SET estado = ? WHERE username = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, estado);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }
}