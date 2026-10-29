package com.belleza.modelo;

public class Barberia extends Servicio {
    private boolean incluyeToallaCaliente;
    private String tipoCorteBarba;

    public Barberia(String id, String nombre, String descripcion, double precio, int duracionMinutos, 
                    boolean incluyeToallaCaliente, String tipoCorteBarba) {
        super(id, nombre, descripcion, precio, duracionMinutos);
        this.incluyeToallaCaliente = incluyeToallaCaliente;
        this.tipoCorteBarba = tipoCorteBarba;
    }

    public boolean isIncluyeToallaCaliente() { return incluyeToallaCaliente; }
    public void setIncluyeToallaCaliente(boolean incluyeToallaCaliente) { this.incluyeToallaCaliente = incluyeToallaCaliente; }
    public String getTipoCorteBarba() { return tipoCorteBarba; }
    public void setTipoCorteBarba(String tipoCorteBarba) { this.tipoCorteBarba = tipoCorteBarba; }

    @Override
    public String getDetalleEspecialidad() {
        return String.format("Especialidad Barbería -> Toalla Caliente: %s | Estilo Barba: %s",
                (incluyeToallaCaliente ? "Sí" : "No"), tipoCorteBarba);
    }
}