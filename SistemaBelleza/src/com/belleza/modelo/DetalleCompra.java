package com.belleza.modelo;

public class DetalleCompra {
    private final Producto producto;
    private final int cantidad;
    private final double precioUnitario;

    public DetalleCompra(Producto producto, int cantidad, double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public double calcularSubtotal() { return cantidad * precioUnitario; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }

    public double getPrecioUnitario() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public double getSubtotal() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}