package com.belleza.modelo;

public class Estetica extends Servicio {
    private String tipoPielRecomendada;
    private boolean requierePruebaAlergia;

    public Estetica(String id, String nombre, String descripcion, double precio, int duracionMinutos, 
                    String tipoPielRecomendada, boolean requierePruebaAlergia) {
        super(id, nombre, descripcion, precio, duracionMinutos);
        this.tipoPielRecomendada = tipoPielRecomendada;
        this.requierePruebaAlergia = requierePruebaAlergia;
    }

    public String getTipoPielRecomendada() { return tipoPielRecomendada; }
    public void setTipoPielRecomendada(String tipoPielRecomendada) { this.tipoPielRecomendada = tipoPielRecomendada; }
    public boolean isRequierePruebaAlergia() { return requierePruebaAlergia; }
    public void setRequierePruebaAlergia(boolean requierePruebaAlergia) { this.requierePruebaAlergia = requierePruebaAlergia; }

    @Override
    public String getDetalleEspecialidad() {
        return String.format("Especialidad Estetica -> Tipo de Piel: %s | Prueba Alergia previa: %s",
                tipoPielRecomendada, (requierePruebaAlergia ? "Requerida" : "No requerida"));
    }
}