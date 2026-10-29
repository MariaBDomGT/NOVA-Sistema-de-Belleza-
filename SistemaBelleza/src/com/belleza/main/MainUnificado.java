package com.belleza.main;

import com.belleza.dao.ConexionBD;
import com.belleza.gui.LoginForm;
import com.belleza.modelo.Barberia;
import com.belleza.modelo.Estetica;
import com.belleza.modelo.Unas;
import com.belleza.servicio.AuthService;
import com.belleza.servicio.GestorServicios;
import com.belleza.servicio.ServidorAPI;

import javax.swing.*;
import java.sql.SQLException;

public class MainUnificado {
    private static final AuthService authService = new AuthService();
    private static final GestorServicios gestorServicios = new GestorServicios();

    public static void main(String[] args) {
        // 1. Inicializar base de datos y tablas locales automáticamente
        inicializarBaseDeDatos();
        
        // 2. Cargar servicios predeterminados
        inicializarServicios();

        // 3. INICIAR EL SERVIDOR API EN SEGUNDO PLANO (Puente para la app móvil / APK)
        new Thread(() -> {
            try {
                ServidorAPI.iniciar(authService);
            } catch (Exception e) {
                System.err.println("Error al arrancar el servidor API: " + e.getMessage());
            }
        }).start();

        // 4. Lanzar la interfaz gráfica de escritorio (Ventanas emergentes / JFrame)
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) {}
            
            // Abre tu ventana de inicio de sesión gráfica existente
            new LoginForm(authService, gestorServicios).setVisible(true);
        });
    }

    private static void inicializarBaseDeDatos() {
        try {
            ConexionBD.configurar("root", "****");
            ConexionBD.crearBaseYTablas();
            authService.cargarDatosDesdeBD();
            
            if (authService.listarUsuarios().isEmpty()) {
                authService.registrar(1, "Jose", "Lux", "admin", "admin123", "ADMIN");
                authService.registrar(2, "Empleado", "Demo", "empleado1", "emp123", "EMPLEADO");
            }
            
            //sirve para insertar las categorias (dama, caballero y niños)
            try(java.sql.Connection c = ConexionBD.obtener();
                java.sql.PreparedStatement ps = c.prepareStatement(
                "INSERT IGNORE INTO categoria_producto (id_categoria, nombre, descripcion) VALUES (?,?,?)")){
                
                ps.setInt(1,1); ps.setString(2, "DAMA"); ps.setString(3,"Producto para Dama"); ps.addBatch();
                ps.setInt(1,2); ps.setString(2, "CABALLERO"); ps.setString(3,"Producto para Caballero"); ps.addBatch();
                ps.setInt(1,3); ps.setString(2, "NIÑOS"); ps.setString(3,"Producto para Niños"); ps.addBatch();
                ps.executeBatch();
            }
            catch(SQLException e){
                System.err.println("Aviso no se pudieron insertar las categorias por defecto: " + e.getMessage());
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión MySQL: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void inicializarServicios() {
        gestorServicios.registrarServicio(new Barberia("BAR-01", "Corte y Perfilado", "Corte clásico y barba", 25.00, 45, true, "Corte Degradado"));
        gestorServicios.registrarServicio(new Estetica("EST-01", "Limpieza Facial", "Exfoliación e hidratación", 45.00, 60, "Todo tipo", true));
        gestorServicios.registrarServicio(new Unas("UNA-01", "Manicura Acrílica", "Uñas acrílicas con diseño", 35.00, 90, "Acrílico Gel", true));
    }
}