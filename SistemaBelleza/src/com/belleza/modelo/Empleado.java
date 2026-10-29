package com.belleza.modelo;

import com.belleza.dao.InventarioDAO;
import com.belleza.servicio.AuthService;
import com.belleza.servicio.GestorServicios;
import com.belleza.util.Factura;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Empleado extends Usuario {
    private static final Scanner entrada = new Scanner(System.in);
    private AuthService auth;
    private GestorServicios gestorServicios;
    private final InventarioDAO inventarioDAO = new InventarioDAO();

    public Empleado(int idUsuario, String nombre, String apellido, String username, String password) {
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

    @Override public String getRol() { return "EMPLEADO"; }
    @Override public void iniciarSesion() {}
    @Override public void cerrarSesion() {}

    @Override
    public void mostrarMenu() {
        int op = 0;
        do {
            System.out.println("\n==================================================");
            System.out.println("   Bienvenido a NOVA - Panel Empleado");
            System.out.println("==================================================");
            System.out.println("1. Registrar Producto en Inventario (MySQL)");
            System.out.println("2. Consultar y Administrar Stock (Eliminar Productos)");
            System.out.println("3. Registrar Venta / Cobrar (Añadir Servicios y Generar Factura)");
            System.out.println("4. Agendar Cita o Reservacion");
            System.out.println("5. Eliminar o Cancelar Cita");
            System.out.println("6. Salir al menu principal");
            System.out.print("Opcion: ");
            try {
                op = Integer.parseInt(entrada.nextLine());
            } catch (NumberFormatException e) {
                op = -1;
            }
            switch (op) {
                case 1 -> registrarProducto();
                case 2 -> consultarYAdministrarStock();
                case 3 -> registrarVentaEIntegrarServicio();
                case 4 -> agendarCita();
                case 5 -> eliminarCita();
                case 6 -> System.out.println("Cerrando sesión de empleado...");
                default -> System.out.println("Opción no valida.");
            }
        } while (op != 6);
    }

    public void registrarProducto() {
        if (auth == null) return;
        System.out.println("\n--- REGISTRAR NUEVO PRODUCTO ---");
        try {
            System.out.print("Nombre del Producto: "); String nombre = entrada.nextLine().trim();
            System.out.print("Descripcion: "); String descripcion = entrada.nextLine().trim();
            System.out.print("Marca: "); String marca = entrada.nextLine().trim();
            System.out.print("Precio Compra (Q): "); double pCompra = Double.parseDouble(entrada.nextLine().trim());
            System.out.print("Precio Venta (Q): "); double pVenta = Double.parseDouble(entrada.nextLine().trim());
            System.out.print("Stock Inicial: "); int stock = Integer.parseInt(entrada.nextLine().trim());
            System.out.print("Stock Mínimo: "); int stockMin = Integer.parseInt(entrada.nextLine().trim());
            System.out.print("Tipo (1 = DAMA, 2 = CABALLERO): ");
            int opTipo = Integer.parseInt(entrada.nextLine().trim());
            TipoProducto tipo = (opTipo == 2) ? TipoProducto.CABALLERO : TipoProducto.DAMA;

            CategoriaProducto cat = new CategoriaProducto(1, "General", "Categoria General");
            Producto p = new Producto(0, nombre, descripcion, marca, pCompra, pVenta, stock, stockMin, cat, tipo);

            inventarioDAO.insertarProducto(p);
            auth.getInventario().add(p);
            System.out.println("Producto guardado correctamente con ID: " + p.getIdProducto());
        } catch (NumberFormatException | SQLException e) {
            System.out.println("Error al registrar el producto: " + e.getMessage());
        }
    }

    public void consultarYAdministrarStock() {
        if (auth == null) return;
        System.out.println("\n--- CONSULTAR STOCK / ELIMINAR PRODUCTO ---");
        List<Producto> inv = auth.getInventario();
        if (inv.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (Producto p : inv) {
            System.out.println("ID: " + p.getIdProducto() + " | Nombre: " + p.getNombre() 
                    + " | Stock: " + p.getStock() + " | Precio Venta: Q" + p.getPrecioVenta());
        }

        System.out.print("\n¿Desea eliminar un producto del stock? (s/n): ");
        if (entrada.nextLine().equalsIgnoreCase("s")) {
            System.out.print("Ingrese el ID del producto a eliminar: ");
            int idDel = Integer.parseInt(entrada.nextLine());
            try {
                inventarioDAO.eliminarProducto(idDel);
                auth.getInventario().removeIf(prod -> prod.getIdProducto() == idDel);
                System.out.println("Producto eliminado exitosamente de la base de datos.");
            } catch (SQLException e) {
                System.out.println("Error al eliminar el producto: " + e.getMessage());
            }
        }
    }

    public void registrarVentaEIntegrarServicio() {
        if (auth == null) return;
        System.out.println("\n--- REGISTRAR VENTA / COBRAR Y FACTURAR ---");
        System.out.print("Nombre del Cliente: ");
        String cliente = entrada.nextLine();

        Venta venta = new Venta(auth.listarVentas().size() + 1, "Efectivo");
        venta.setCliente(cliente);
        List<String[]> itemsFactura = new ArrayList<>();

        String resp;
        do {
            System.out.println("\n¿Que desea vender?");
            System.out.println("1. Producto de Stock");
            System.out.println("2. Servicio del Catalogo");
            System.out.print("Opcion: ");
            int tipoOp = Integer.parseInt(entrada.nextLine());

            if (tipoOp == 1) {
                System.out.print("ID o Nombre del Producto: ");
                String busqueda = entrada.nextLine();
                Producto productoEncontrado = null;
                for (Producto p : auth.getInventario()) {
                    if (p.getNombre().equalsIgnoreCase(busqueda) || String.valueOf(p.getIdProducto()).equals(busqueda)) {
                        productoEncontrado = p;
                        break;
                    }
                }

                if (productoEncontrado != null) {
                    System.out.print("Cantidad: ");
                    int cant = Integer.parseInt(entrada.nextLine());
                    try {
                        productoEncontrado.actualizarStock(-cant);
                        inventarioDAO.actualizarStock(productoEncontrado.getIdProducto(), productoEncontrado.getStock());

                        double subtotal = productoEncontrado.getPrecioVenta() * cant;
                        double ganancia = (productoEncontrado.getPrecioVenta() - productoEncontrado.getPrecioCompra()) * cant;

                        DetalleVenta dv = new DetalleVenta(cant, productoEncontrado.getPrecioVenta());
                        dv.setGanancia(ganancia);
                        venta.agregarDetalle(dv);

                        itemsFactura.add(new String[]{productoEncontrado.getNombre(), String.valueOf(cant), String.valueOf(productoEncontrado.getPrecioVenta()), String.valueOf(subtotal)});
                        System.out.println("Stock descontado. Quedan: " + productoEncontrado.getStock());
                    } catch (SQLException e) {
                        System.out.println("X " + e.getMessage());
                    }
                } else {
                    System.out.println("Producto no encontrado.");
                }
            } else if (tipoOp == 2) {
                if (gestorServicios == null) gestorServicios = new GestorServicios();
                List<Servicio> servicios = gestorServicios.listarDisponibles();
                if (servicios.isEmpty()) {
                    System.out.println("No hay servicios disponibles.");
                } else {
                    System.out.println("\n--- SERVICIOS DISPONIBLES ---");
                    for (int i = 0; i < servicios.size(); i++) {
                        Servicio s = servicios.get(i);
                        System.out.println((i + 1) + ". " + s.getNombre() + " - Q" + s.getPrecio() + " (" + s.getDuracionMinutos() + " min)");
                    }
                    System.out.print("Seleccione el numero del servicio: ");
                    int idx = Integer.parseInt(entrada.nextLine()) - 1;
                    if (idx >= 0 && idx < servicios.size()) {
                        Servicio servSel = servicios.get(idx);
                        DetalleVenta dv = new DetalleVenta(1, servSel.getPrecio());
                        dv.setGanancia(servSel.getPrecio() * 0.70); 
                        venta.agregarDetalle(dv);

                        itemsFactura.add(new String[]{servSel.getNombre(), "1", String.valueOf(servSel.getPrecio()), String.valueOf(servSel.getPrecio())});
                        System.out.println("Servicio agregado a la factura.");
                    }
                }
            }

            System.out.print("¿Desea agregar otro ítem o servicio? (s/n): ");
            resp = entrada.nextLine();
        } while (resp.equalsIgnoreCase("s"));

        auth.agregarVenta(venta);

        Factura f = new Factura(venta.getIdVenta(), "FAC-00" + venta.getIdVenta(), venta.getFecha(), venta.getTotal());
        f.exportarPDF("Factura_" + venta.getIdVenta() + ".pdf", cliente, itemsFactura);
        System.out.println("Factura generada con éxito: 'Factura_" + venta.getIdVenta() + ".pdf'");
    }

    public void agendarCita() {
        System.out.println("\n--- AGENDAR RESERVACIÓN ---");
        try {
            System.out.print("Nombre del Cliente: "); String cliente = entrada.nextLine();
            System.out.print("Servicio: "); String servicio = entrada.nextLine();
            System.out.print("Precio Servicio (Q): "); double precio = Double.parseDouble(entrada.nextLine());
            System.out.print("Fecha (YYYY-MM-DD): "); java.time.LocalDate fecha = java.time.LocalDate.parse(entrada.nextLine());
            System.out.print("Hora (HH:MM): "); java.time.LocalTime hora = java.time.LocalTime.parse(entrada.nextLine());

            Cita nuevaCita = new Cita(0, cliente, getNombre(), servicio, precio, fecha, hora);
            auth.agregarCita(nuevaCita);
            System.out.println("Reservación agendada y guardada en MySQL: " + nuevaCita);
        } catch (NumberFormatException e) {
            System.out.println("Error al agendar cita: " + e.getMessage());
        }
    }

    public void eliminarCita() {
        System.out.println("\n--- ELIMINAR / CANCELAR CITA ---");
        System.out.print("Ingrese el ID de la cita a eliminar: ");
        int idCita = Integer.parseInt(entrada.nextLine());
        if (auth.eliminarCita(idCita)) {
            System.out.println("Cita eliminada de la base de datos.");
        } else {
            System.out.println("No se encontró la cita especificada.");
        }
    }
}