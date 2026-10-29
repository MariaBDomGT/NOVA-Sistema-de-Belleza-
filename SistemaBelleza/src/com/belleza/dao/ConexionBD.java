package com.belleza.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {
    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    public static final String BASE = "salon_belleza";
    private static final String OPCIONES = 
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    
    private static String usuario = "root";
    private static String clave = "****"; 

    private static final String[] TABLAS = {
        "CREATE TABLE IF NOT EXISTS usuario ("
            + " id_usuario INT AUTO_INCREMENT PRIMARY KEY,"
            + " nombre VARCHAR(100) NOT NULL,"
            + " apellido VARCHAR(100) NOT NULL,"
            + " username VARCHAR(50) NOT NULL UNIQUE,"
            + " password VARCHAR(255) NOT NULL,"
            + " rol ENUM('ADMIN', 'EMPLEADO') NOT NULL,"
            + " estado TINYINT(1) NOT NULL DEFAULT 1"
            + ") ENGINE=InnoDB",

        "CREATE TABLE IF NOT EXISTS categoria_producto ("
            + " id_categoria INT AUTO_INCREMENT PRIMARY KEY,"
            + " nombre VARCHAR(100) NOT NULL UNIQUE,"
            + " descripcion VARCHAR(255)"
            + ") ENGINE=InnoDB",

        "CREATE TABLE IF NOT EXISTS producto ("
            + " id_producto INT AUTO_INCREMENT PRIMARY KEY,"
            + " nombre VARCHAR(100) NOT NULL,"
            + " descripcion VARCHAR(255),"
            + " marca VARCHAR(100),"
            + " precio_compra DECIMAL(10,2) NOT NULL DEFAULT 0,"
            + " precio_venta DECIMAL(10,2) NOT NULL DEFAULT 0,"
            + " stock INT NOT NULL DEFAULT 0,"
            + " stock_minimo INT NOT NULL DEFAULT 0,"
            + " estado TINYINT(1) NOT NULL DEFAULT 1,"
            + " tipo ENUM('DAMA','CABALLERO', 'NIÑOS') NOT NULL,"
            + " id_categoria INT NULL,"
            + " FOREIGN KEY (id_categoria) REFERENCES categoria_producto(id_categoria)"
            + ") ENGINE=InnoDB",

        "CREATE TABLE IF NOT EXISTS servicio ("
            + " id_servicio VARCHAR(50) PRIMARY KEY,"
            + " categoria VARCHAR(50) NOT NULL,"
            + " nombre VARCHAR(100) NOT NULL,"
            + " descripcion VARCHAR(255),"
            + " precio DECIMAL(10,2) NOT NULL,"
            + " duracion_minutos INT NOT NULL,"
            + " activo TINYINT(1) NOT NULL DEFAULT 1,"
            + " especialidad_detalle VARCHAR(255)"
            + ") ENGINE=InnoDB",

        "CREATE TABLE IF NOT EXISTS cita ("
            + " id_cita INT AUTO_INCREMENT PRIMARY KEY,"
            + " cliente VARCHAR(100) NOT NULL,"
            + " empleado VARCHAR(100) NOT NULL,"
            + " servicio VARCHAR(100) NOT NULL,"
            + " precio_servicio DECIMAL(10,2) NOT NULL,"
            + " fecha DATE NOT NULL,"
            + " hora TIME NOT NULL,"
            + " estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE'"
            + ") ENGINE=InnoDB",

        "CREATE TABLE IF NOT EXISTS venta ("
            + " id_venta INT AUTO_INCREMENT PRIMARY KEY,"
            + " fecha DATETIME NOT NULL,"
            + " cliente VARCHAR(100),"
            + " total DECIMAL(10,2) NOT NULL,"
            + " metodo_pago VARCHAR(50) NOT NULL"
            + ") ENGINE=InnoDB",

        "CREATE TABLE IF NOT EXISTS detalle_venta ("
            + " id_detalle INT AUTO_INCREMENT PRIMARY KEY,"
            + " id_venta INT NOT NULL,"
            + " id_producto INT NULL,"
            + " id_servicio VARCHAR(50) NULL,"
            + " concepto VARCHAR(150) NOT NULL,"
            + " cantidad INT NOT NULL,"
            + " precio_unitario DECIMAL(10,2) NOT NULL,"
            + " subtotal DECIMAL(10,2) NOT NULL,"
            + " FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE"
            + ") ENGINE=InnoDB"
    };

    public static void configurar(String nuevoUsuario, String nuevaClave) {
        usuario = nuevoUsuario;
        clave = nuevaClave;
    }

    public static void registrarDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("Driver JDBC de MySQL no encontrado en las librerias.");
            }
        }
    }

    public static void crearBaseYTablas() throws SQLException {
        registrarDriver();
        String urlServidor = "jdbc:mysql://" + HOST + ":" + PUERTO + OPCIONES;
        try (Connection c = DriverManager.getConnection(urlServidor, usuario, clave);
             Statement st = c.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + BASE + " CHARACTER SET utf8mb4");
        }
        try (Connection c = obtener(); Statement st = c.createStatement()) {
            for (String sql : TABLAS) {
                st.executeUpdate(sql);
            }
        }
    }

    public static Connection obtener() throws SQLException {
        registrarDriver();
        String urlBaseDatos = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE + OPCIONES;
        return DriverManager.getConnection(urlBaseDatos, usuario, clave);
    }
}