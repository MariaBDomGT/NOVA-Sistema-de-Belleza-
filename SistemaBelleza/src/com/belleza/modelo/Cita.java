package com.belleza.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private int idCita;
    private String cliente;
    private String empleado;
    private String servicio;
    private double precioServicio;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado; 

    public Cita(int idCita, LocalDate fecha, LocalTime hora, String servicio) {
        this.idCita = idCita;
        this.fecha = fecha;
        this.hora = hora;
        this.servicio = servicio;
        this.cliente = "Cliente General";
        this.empleado = "Empleado Asignado";
        this.precioServicio = 0.0;
        this.estado = "PENDIENTE";
    }

    public Cita(int idCita, String cliente, String empleado, String servicio, double precioServicio, LocalDate fecha, LocalTime hora) {
        this.idCita = idCita;
        this.cliente = cliente;
        this.empleado = empleado;
        this.servicio = servicio;
        this.precioServicio = precioServicio;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = "PENDIENTE";
    }

    Cita(int id, String cliente, String nombre, String servicio, double precio, String fecha) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getEmpleado() { return empleado; }
    public void setEmpleado(String empleado) { this.empleado = empleado; }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public double getPrecioServicio() { return precioServicio; }
    public void setPrecioServicio(double precioServicio) { this.precioServicio = precioServicio; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    @Override
    public String toString() {
        return "Cita #" + idCita + " | Cliente: " + cliente + " | Servicio: " + servicio + " | Fecha: " + fecha + " " + hora + " | Estado: " + estado;
    }
}