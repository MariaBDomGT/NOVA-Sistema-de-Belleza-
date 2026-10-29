package com.belleza.modelo;

public class Cliente {
    private final int idCliente;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;

    public Cliente(int idCliente, String nombre, String apellido, String telefono, String correo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getIdCliente() { return idCliente; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }

    public void registrarCliente() {
        System.out.println("Cliente Registrado con ID: " + idCliente);
    }

    public void actualizarDatos(String nuevoNombre, String nuevoApellido, String nuevoTelefono, String nuevoCorreo) {
        if (nuevoNombre != null && !nuevoNombre.isBlank()) this.nombre = nuevoNombre;
        if (nuevoApellido != null && !nuevoApellido.isBlank()) this.apellido = nuevoApellido;
        if (nuevoTelefono != null && !nuevoTelefono.isBlank()) this.telefono = nuevoTelefono;
        if (nuevoCorreo != null && !nuevoCorreo.isBlank()) this.correo = nuevoCorreo;
    }

    @Override
    public String toString() {
        return String.format("#%d | %s %s | Tel: %s | %s", idCliente, nombre, apellido, telefono, correo);
    }
}