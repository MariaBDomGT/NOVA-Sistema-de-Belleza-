package com.belleza.modelo;

import java.util.List;

public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private String marca;
    private double precioCompra;
    private double precioVenta;
    private int stock;
    private int stockMinimo;
    private boolean estado;
    private final CategoriaProducto categoria;
    private final TipoProducto tipo;

    public Producto(int idProducto, String nombre, String descripcion, String marca,
                    double precioCompra, double precioVenta, int stock, int stockMinimo,
                    CategoriaProducto categoria, TipoProducto tipo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.categoria = categoria;
        this.tipo = tipo;
        this.estado = true;
    }

    public Producto() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void actualizarStock(int cantidad) {
        if (!estado) throw new IllegalStateException("El producto " + nombre + " esta inactivo.");
        if (stock + cantidad < 0) {
            throw new IllegalArgumentException("Stock insuficiente de " + nombre + ". Stock actual: " + stock);
        }
        this.stock += cantidad;
    }

    public int consultarStock() { return stock; }

    public void guardarProducto(List<Producto> lista) {
        for (Producto p : lista) {
            if (p.getIdProducto() == idProducto) {
                throw new IllegalArgumentException("Ya existe un producto con ID " + idProducto);
            }
        }
        lista.add(this);
    }

    public void actualizarProducto(String nombre, String descripcion, String marca,
                                   double precioCompra, double precioVenta, int stockMinimo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockMinimo = stockMinimo;
    }

    public void eliminarProducto() { this.estado = false; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getMarca() { return marca; }
    public double getPrecioCompra() { return precioCompra; }
    public double getPrecioVenta() { return precioVenta; }
    public int getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }
    public CategoriaProducto getCategoria() { return categoria; }
    public TipoProducto getTipo() { return tipo; }
    public boolean isEstado() { return estado; }

    @Override
    public String toString() {
        return "[" + idProducto + "] " + nombre + " | Marca: " + marca + " | Precio Venta: Q" + precioVenta + " | Stock: " + stock;
    }

    public void setNombre(String nombre) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setDescripcion(String descripcion) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setPrecioCompra(double precioCompra) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setPrecioVenta(double precioVenta) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setStock(int stock) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setStockMinimo(int stockMinimo) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setCategoria(CategoriaProducto catDefault) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setMarca(String marca) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}