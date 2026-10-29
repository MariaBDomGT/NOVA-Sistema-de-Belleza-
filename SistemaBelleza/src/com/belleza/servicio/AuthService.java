package com.belleza.servicio;

import com.belleza.dao.*;
import com.belleza.modelo.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class AuthService {
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final List<Cita> citas = new ArrayList<>();
    private final List<Producto> inventario = new ArrayList<>();
    private final List<Venta> ventas = new ArrayList<>();

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final CitaDAO citaDAO = new CitaDAO();
    private final InventarioDAO inventarioDAO = new InventarioDAO();

    public void cargarDatosDesdeBD() {
        try {
            usuarios.clear();
            for (Usuario u : usuarioDAO.cargarUsuarios(this)) {
                usuarios.put(u.getUsername(), u);
            }
            citas.clear();
            citas.addAll(citaDAO.cargarCitas());

            List<CategoriaProducto> cats = inventarioDAO.cargarCategorias();
            inventario.clear();
            inventario.addAll(inventarioDAO.cargarProductos(cats));
        } catch (SQLException e) {
            System.err.println("Error al sincronizar con MySQL: " + e.getMessage());
        }
    }

    public boolean registrar(int idUsuario, String nombre, String apellido, String username, String password, String tipo) {
        if (usuarios.containsKey(username)) return false;
        String hash = hash(password);
        Usuario nuevo;
        if (tipo.equalsIgnoreCase("ADMIN")) {
            Administrador admin = new Administrador(idUsuario, nombre, apellido, username, hash);
            admin.setAuthService(this);
            nuevo = admin;
        } else {
            Empleado emp = new Empleado(idUsuario, nombre, apellido, username, hash);
            emp.setAuthService(this);
            nuevo = emp;
        }
        
        try {
            usuarioDAO.insertarUsuario(nuevo);
            usuarios.put(username, nuevo);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario en BD: " + e.getMessage());
            return false;
        }
    }

    public Usuario login(String username, String password) {
        Usuario u = usuarios.get(username);
        if (u == null || !u.isEstado()) return null;
        if (!u.getPassword().equals(hash(password))) return null;
        return u;
    }

    public String hash(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Usuario> listarUsuarios() { return usuarios.values(); }
    public Usuario buscarUsuario(String username) { return usuarios.get(username); }

    public boolean eliminarUsuario(String username) {
        try {
            usuarioDAO.eliminarUsuarioBD(username);
            return usuarios.remove(username) != null;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean estadoUsuario(String username, boolean nuevoEstado) {
        Usuario u = usuarios.get(username);
        if (u != null) {
            try {
                usuarioDAO.actualizarEstadoBD(username, nuevoEstado);
                u.setEstado(nuevoEstado);
                return true;
            } catch (SQLException e) {
                System.err.println("Error al actualizar estado: " + e.getMessage());
            }
        }
        return false;
    }

    public List<Cita> listarCitas() { return citas; }
    
    public void agregarCita(Cita c) { 
        try {
            citaDAO.guardarCita(c);
            citas.add(c);
        } catch (SQLException e) {
            System.err.println("Error al guardar cita en BD: " + e.getMessage());
        }
    }
    
    public void actualizarCita(int idCita, String nuevoEstado){
        String sql = "UPDATE cita SET estado = ? WHERE id_cita = ?";
        try (Connection c = ConexionBD.obtener(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idCita);
            ps.executeUpdate();
            
            for(Cita cita : citas){
                if(cita.getIdCita() == idCita){
                    cita.setEstado(nuevoEstado);
                }
            }
        }
        catch(SQLException e){
            System.err.println("Error al actualizar la cita de la BD: " + e.getMessage());
        }
    }

    public boolean eliminarCita(int idCita) {
        try {
            citaDAO.eliminarCita(idCita);
            citas.removeIf(c -> c.getIdCita() == idCita);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cita: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> getInventario() { return inventario; }
    public List<Venta> listarVentas() { return ventas; }
    public void agregarVenta(Venta v) { ventas.add(v); }
}