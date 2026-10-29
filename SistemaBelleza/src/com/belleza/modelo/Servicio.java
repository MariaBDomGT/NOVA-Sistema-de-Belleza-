package com.belleza.modelo;

public abstract class Servicio {
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int duracionMinutos;
    private boolean activo;

    public Servicio(String id, String nombre, String descripcion, double precio, int duracionMinutos) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El ID es obligatorio.");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio.");
        setPrecio(precio);
        setDuracionMinutos(duracionMinutos);
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPrecio() { return precio; }
    
    public final void setPrecio(double precio) {
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.precio = precio;
    }

    public int getDuracionMinutos() { return duracionMinutos; }
    
    public final void setDuracionMinutos(int duracionMinutos) {
        if (duracionMinutos <= 0) throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos.");
        this.duracionMinutos = duracionMinutos;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public abstract String getDetalleEspecialidad();

    @Override
    public String toString() {
        return String.format("[%s] %s | Q%.2f | %d min | Estado: %s%n   %s",
                id, nombre, precio, duracionMinutos, (activo ? "Disponible" : "No disponible"), getDetalleEspecialidad());
    }
}