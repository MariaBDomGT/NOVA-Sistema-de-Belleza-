package com.belleza.modelo;

public class Unas extends Servicio {
    private String tipoTecnica;
    private boolean incluyeDisenoGrafico;

    public Unas(String id, String nombre, String descripcion, double precio, int duracionMinutos, 
                String tipoTecnica, boolean incluyeDisenoGrafico) {
        super(id, nombre, descripcion, precio, duracionMinutos);
        this.tipoTecnica = tipoTecnica;
        this.incluyeDisenoGrafico = incluyeDisenoGrafico;
    }

    public String getTipoTecnica() { return tipoTecnica; }
    public void setTipoTecnica(String tipoTecnica) { this.tipoTecnica = tipoTecnica; }
    public boolean isIncluyeDisenoGrafico() { return incluyeDisenoGrafico; }
    public void setIncluyeDisenoGrafico(boolean incluyeDisenoGrafico) { this.incluyeDisenoGrafico = incluyeDisenoGrafico; }

    @Override
    public String getDetalleEspecialidad() {
        return String.format("Especialidad Uñas -> Tccnica: %s | Diseño Incluido: %s",
                tipoTecnica, (incluyeDisenoGrafico ? "Sí" : "No"));
    }
}