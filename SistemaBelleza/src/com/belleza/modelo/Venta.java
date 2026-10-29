package com.belleza.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Venta {
    private final int idVenta;
    private final Date fecha;
    private String cliente;
    private double total;
    private double gananciaVenta;
    private final String metodoPago;
    private final List<DetalleVenta> detalles;

    public Venta(int idVenta, String metodoPago) {
        this.idVenta = idVenta;
        this.fecha = new Date();
        this.metodoPago = metodoPago;
        this.detalles = new ArrayList<>();
        this.total = 0.0;
        this.gananciaVenta = 0.0;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle != null) {
            this.detalles.add(detalle);
            this.total += detalle.getSubtotal();
            this.gananciaVenta += detalle.getGanancia();
        }
    }

    public int getIdVenta() { return idVenta; }
    public Date getFecha() { return fecha; }

    public String getCliente() { 
        return cliente; 
    }

    public void setCliente(String cliente) { 
        this.cliente = cliente; 
    }

    public double getTotal() { return total; }
    public double getGananciaVenta() { return gananciaVenta; }
    public String getMetodoPago() { return metodoPago; }
    public List<DetalleVenta> getDetalles() { return detalles; }
}