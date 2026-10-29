package com.belleza.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Compra {

    private int idCompra;
    private LocalDateTime fechaHora;
    private double total;
    private List<DetalleCompra> detalles;

    public Compra(int idCompra, LocalDateTime fechaHora, double total) {
        this.idCompra = idCompra;
        this.fechaHora = fechaHora;
        this.total = total;
        this.detalles = new ArrayList<>();
    }

    public Compra() {
        this.fechaHora = LocalDateTime.now();
        this.detalles = new ArrayList<>();
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(DetalleCompra detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
        this.total += detalle.getSubtotal();
    }
}