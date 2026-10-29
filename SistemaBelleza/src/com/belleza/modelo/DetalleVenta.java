package com.belleza.modelo;

public final class DetalleVenta {
    private final int cantidad;
    private final double precioUnitario;
    private final double subtotal;
    private double ganancia;

    public DetalleVenta(int cantidad, double precioUnitario) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    public double getSubtotal() { return subtotal; }
    public double getGanancia() { return ganancia; }
    public void setGanancia(double ganancia) { this.ganancia = ganancia; }
}