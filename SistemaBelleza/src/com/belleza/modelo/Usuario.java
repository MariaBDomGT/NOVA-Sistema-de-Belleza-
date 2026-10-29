package com.belleza.modelo;

public abstract class Usuario {
    private int idUsuario;
    private String nombre;
    private String apellido;
    private boolean estado;
    protected String username; 
    protected String password;

    public Usuario(int idUsuario, String nombre, String apellido, String username, String password) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.password = password;
        this.estado = true;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public abstract void iniciarSesion();
    public abstract void cerrarSesion();
    public abstract String getRol();
    public abstract void mostrarMenu();
}