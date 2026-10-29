package com.belleza.dao;

import com.belleza.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarioDAO {

    public List<CategoriaProducto> cargarCategorias() throws SQLException {
        List<CategoriaProducto> lista = new ArrayList<>();
        try (Connection c = ConexionBD.obtener();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_categoria, nombre, descripcion FROM categoria_producto ORDER BY id_categoria")) {
            while (rs.next()) {
                lista.add(new CategoriaProducto(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre"), 
                        rs.getString("descripcion")
                ));
            }
        }
        return lista;
    }

    public void insertarCategoria(CategoriaProducto cat) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO categoria_producto (nombre, descripcion) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cat.getNombre());
            ps.setString(2, cat.getDescripcion());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    cat.setIdCategoria(keys.getInt(1));
                }
            }
        }
    }

    public List<Producto> cargarProductos(List<CategoriaProducto> categorias) throws SQLException {
        Map<Integer, CategoriaProducto> porId = new HashMap<>();
        for (CategoriaProducto cat : categorias) {
            porId.put(cat.getIdCategoria(), cat);
        }
        List<Producto> lista = new ArrayList<>();
        try (Connection c = ConexionBD.obtener();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT id_producto, nombre, descripcion, marca, precio_compra, precio_venta,"
                     + " stock, stock_minimo, estado, tipo, id_categoria FROM producto WHERE estado = 1 ORDER BY id_producto")) {
            while (rs.next()) {
                int idCat = rs.getInt("id_categoria");
                CategoriaProducto cat = rs.wasNull() ? null : porId.get(idCat);
                Producto p = new Producto(
                        rs.getInt("id_producto"), 
                        rs.getString("nombre"),
                        rs.getString("descripcion"), 
                        rs.getString("marca"),
                        rs.getDouble("precio_compra"), 
                        rs.getDouble("precio_venta"),
                        rs.getInt("stock"), 
                        rs.getInt("stock_minimo"),
                        cat, 
                        TipoProducto.valueOf(rs.getString("tipo"))
                );
                lista.add(p);
            }
        }
        return lista;
    }
    
    

    public void insertarProducto(Producto p) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO producto (nombre, descripcion, marca, precio_compra, precio_venta,"
                     + " stock, stock_minimo, estado, tipo, id_categoria) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            llenarProducto(ps, p);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setIdProducto(keys.getInt(1));
                }
            }
        }
    }

    public void actualizarStock(int idProducto, int nuevoStock) throws SQLException {
        String sql = "UPDATE producto SET stock = ? WHERE id_producto = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    public void eliminarProducto(int idProducto) throws SQLException {
        String sql = "UPDATE producto SET estado = 0 WHERE id_producto = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }

    private void llenarProducto(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getDescripcion());
        ps.setString(3, p.getMarca());
        ps.setDouble(4, p.getPrecioCompra());
        ps.setDouble(5, p.getPrecioVenta());
        ps.setInt(6, p.getStock());
        ps.setInt(7, p.getStockMinimo());
        ps.setBoolean(8, p.isEstado());
        ps.setString(9, p.getTipo().name());
        if (p.getCategoria() == null) {
            ps.setNull(10, Types.INTEGER);
        } else {
            ps.setInt(10, p.getCategoria().getIdCategoria());
        }
    }

    public void agregarProducto(Producto nuevo) throws SQLException {
        insertarProducto(nuevo);
    }
}