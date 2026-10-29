package com.belleza.modelo;

import com.belleza.gui.CatalogoForm;
import com.belleza.servicio.AuthService;
import com.belleza.servicio.GestorServicios;
import com.belleza.servicio.Metrica;
import com.belleza.util.Reporte;

import java.util.Scanner;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class Administrador extends Usuario {
    private static final Scanner entrada = new Scanner(System.in);
    private AuthService auth;
    private GestorServicios gestorServicios;
    private Administrador administrador;

    public Administrador(int idUsuario, String nombre, String apellido, String username, String password) {
        super(idUsuario, nombre, apellido, username, password);
    }

    public void setAuthService(AuthService auth) { 
        this.auth = auth; 
    }

    public void setGestorServicios(GestorServicios gestorServicios) {
        this.gestorServicios = gestorServicios;
    }

    public GestorServicios getGestorServicios() {
        return gestorServicios;
    }

    @Override public String getRol() { return "ADMIN"; }
    @Override public void iniciarSesion() {}
    @Override public void cerrarSesion() {}

    @Override 
    public void mostrarMenu() {
        int op = 0;
        do {
            System.out.println("\n==================================================");
            System.out.println("   Bienvenido a NOVA - Panel Administrador");
            System.out.println("==================================================");
            System.out.println("1. Gestionar usuarios (Crear/Eliminar/Contraseñas)");
            System.out.println("2. Consultar metricas en tiempo real (Citas)");
            System.out.println("3. Consultar ventas y calculo de GANANCIAS");
            System.out.println("4. Abrir Catalogo Grafico de Servicios (GUI)");
            System.out.println("5. Generar reportes financieros en Excel (.xlsx)");
            System.out.println("6. Salir al menu principal");
            System.out.print("Opción: ");
            try {
                op = Integer.parseInt(entrada.nextLine());
            } catch (NumberFormatException e) {
                op = -1;
            }
            switch (op) {
                case 1 -> gestionarUsuarios();
                case 2 -> consultarMetricas();
                case 3 -> consultarVentasYGanancias();
                case 4 -> abrirGuiServicios();
                case 5 -> generarReportes();
                case 6 -> System.out.println("Cerrando sesion de administrador...");
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 6);
    }

    private void abrirGuiServicios() {
        if (gestorServicios == null) gestorServicios = new GestorServicios();
        javax.swing.SwingUtilities.invokeLater(() -> new CatalogoForm(administrador, auth, gestorServicios).setVisible(true));
    }

    public void gestionarUsuarios() {
        if (auth == null) return;
        int op = 0;
        do {
            System.out.println("\n====== Gestión de Usuarios ======");
            System.out.println("1. Listar Usuarios");
            System.out.println("2. Registrar Nuevo Usuario");
            System.out.println("3. Cambiar Estado del Usuario");
            System.out.println("4. Eliminar Usuario");
            System.out.println("5. Volver");
            System.out.print("Opción: ");
            try {
                op = Integer.parseInt(entrada.nextLine());
            } catch (NumberFormatException e) {
                continue;
            }
            switch (op) {
                case 1 -> listarUsuarios();
                case 2 -> registrarNuevoUsuario();
                case 3 -> estadoUsuario();
                case 4 -> eliminarUsuario();
                case 5 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción invalida.");
            }
        } while (op != 5);
    }

    private void registrarNuevoUsuario() {
        System.out.print("Nombre: "); String nom = entrada.nextLine();
        System.out.print("Apellido: "); String ape = entrada.nextLine();
        System.out.print("Username: "); String user = entrada.nextLine();
        System.out.print("Contraseña: "); String pass = entrada.nextLine();
        System.out.print("Rol (1 = ADMIN, 2 = EMPLEADO): ");
        int opRol = Integer.parseInt(entrada.nextLine());
        String rol = (opRol == 1) ? "ADMIN" : "EMPLEADO";

        if (auth.registrar(0, nom, ape, user, pass, rol)) {
            System.out.println("Usuario guardado con éxito en la base de datos.");
        } else {
            System.out.println("No se pudo crear el usuario (el nombre de usuario ya existe).");
        }
    }

    public void consultarMetricas() {
        if (auth == null) return;
        Metrica m = new Metrica(auth.listarCitas());
        System.out.println("\n====== Metricas del Negocio (Datos de Citas) ======");
        System.out.println("Total de Citas Registradas: " + m.totalCitas());
        System.out.printf("Tasa de Cancelación: %.2f%%%n", m.porcentajeCancelacion());
        System.out.printf("Promedio Citas por Dia: %.2f%n", m.promedioCitasPorDia());
        System.out.println("\n====== Desglose por Estado ======");
        m.citasPorEstado().forEach((k, v) -> System.out.printf("%-15s : %d%n", k, v));
        System.out.println("Dia más ocupado: " + m.diaMasOcupado());
        System.out.println("Hora más solicitada: " + m.horaMasSolicitada());
    }

    public void consultarVentasYGanancias() {
        if (auth == null) return;
        List<Venta> ventas = auth.listarVentas();
        System.out.println("\n====== Registro General de Ventas y Ganancias ======");
        if (ventas.isEmpty()) {
            System.out.println("No se han registrado ventas aun.");
            return;
        }

        double ingresosTotales = 0;
        double gananciaNetaTotal = 0;

        for (Venta v : ventas) {
            ingresosTotales += v.getTotal();
            gananciaNetaTotal += v.getGananciaVenta();
            System.out.printf("ID Venta: %d | Cliente: %s | Total: Q%.2f | Ganancia Est.: Q%.2f%n",
                    v.getIdVenta(), v.getCliente(), v.getTotal(), v.getGananciaVenta());
        }

        System.out.println("--------------------------------------------------");
        System.out.printf("INGRESOS BRUTOS TOTALES: Q%.2f%n", ingresosTotales);
        System.out.printf("GANANCIA NETA ESTIMADA:  Q%.2f%n", gananciaNetaTotal);
        System.out.println("--------------------------------------------------");
    }

    public void generarReportes() {
        if (auth == null) return;
        System.out.println("\n====== Generador de Reporte Financiero (.xlsx) ======");
        Reporte reporte = new Reporte(1, "Ventas_y_Ganancias", new java.util.Date(), new java.util.Date(), "EXCEL");
        String[] encabezados = {"ID Venta", "Fecha", "Cliente", "Total (Q)", "Ganancia Neta (Q)"};
        List<Object[]> datos = new ArrayList<>();

        for (Venta v : auth.listarVentas()) {
            datos.add(new Object[]{v.getIdVenta(), v.getFecha().toString(), v.getCliente(), v.getTotal(), v.getGananciaVenta()});
        }

        boolean exito = reporte.exportarExcelNativo("Reporte_NOVA_Financiero.xlsx", encabezados, datos);
        if (exito) {
            System.out.println("Reporte exportado exitosamente como 'Reporte_NOVA_Financiero.xlsx'.");
        } else {
            System.out.println("Ocurrió un error al generar el archivo Excel.");
        }
    }

    private void listarUsuarios() {
        Collection<Usuario> lista = auth.listarUsuarios();
        System.out.printf("%-5s %-15s %-20s %-12s %-10s%n", "ID", "USERNAME", "NOMBRE COMPLETO", "ROL", "ESTADO");
        System.out.println("-----------------------------------------------------------------------------------");
        for (Usuario u : lista) {
            System.out.printf("%-5s %-15s %-20s %-12s %-10s%n",
                    u.getIdUsuario(), u.getUsername(), u.getNombre() + " " + u.getApellido(),
                    u.getRol(), u.isEstado() ? "ACTIVO" : "INACTIVO");
        }
    }

    private void estadoUsuario() {
        System.out.print("Username del usuario: "); String u = entrada.nextLine();
        Usuario usr = auth.buscarUsuario(u);
        if (usr != null) {
            auth.estadoUsuario(u, !usr.isEstado());
            System.out.println("Estado actualizado correctamente.");
        }
    }

    private void eliminarUsuario() {
        System.out.print("Username a eliminar: "); String u = entrada.nextLine();
        if (auth.eliminarUsuario(u)) {
            System.out.println("Usuario eliminado.");
        }
    }
}