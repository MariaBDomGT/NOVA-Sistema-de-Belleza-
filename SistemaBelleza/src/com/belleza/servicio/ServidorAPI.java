package com.belleza.servicio;

import com.belleza.dao.CitaDAO;
import com.belleza.dao.InventarioDAO;
import com.belleza.modelo.*;
import com.belleza.util.Factura;
import com.belleza.util.Reporte;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ServidorAPI {
    private static final InventarioDAO invDao = new InventarioDAO();
    private static final CitaDAO citaDao = new CitaDAO();
    private static AuthService authService;

    public static void iniciar(AuthService auth) {
        authService = auth;
        iniciar();
    }

    public static void iniciar() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);
            
            // 1. Obtener Productos (GET) y Agregar Producto (POST) en /api/productos[cite: 2]
            server.createContext("/api/productos", (HttpExchange exchange) -> {
                configurarCORS(exchange);
                String method = exchange.getRequestMethod();
                
                if ("GET".equalsIgnoreCase(method)) {
                    try {
                        List<CategoriaProducto> cats = invDao.cargarCategorias();
                        List<Producto> productos = invDao.cargarProductos(cats);
                        
                        StringBuilder json = new StringBuilder("[");
                        for (int i = 0; i < productos.size(); i++) {
                            Producto p = productos.get(i);
                            json.append(String.format(Locale.US,
                                    "{\"idProducto\":%d,\"nombre\":\"%s\",\"precioVenta\":%.2f,\"stock\":%d,\"stockMinimo\":%d,\"marca\":\"%s\"}",
                                    p.getIdProducto(), p.getNombre(), p.getPrecioVenta(), p.getStock(), p.getStockMinimo(), p.getMarca() != null ? p.getMarca() : ""
                            ));
                            if (i < productos.size() - 1) json.append(",");
                        }
                        json.append("]");
                        
                        enviarRespuesta(exchange, 200, json.toString());
                    } catch (IOException | SQLException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                    }
                } else if ("POST".equalsIgnoreCase(method)) {
                    try {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        
                        String nombre = extraerValorJson(body, "nombre");
                        double precioVenta = Double.parseDouble(extraerValorJson(body, "precioVenta").isEmpty() ? "0" : extraerValorJson(body, "precioVenta"));
                        double precioCompra = Double.parseDouble(extraerValorJson(body, "precioCompra").isEmpty() ? "0" : extraerValorJson(body, "precioCompra"));
                        int stock = Integer.parseInt(extraerValorJson(body, "stock").isEmpty() ? "0" : extraerValorJson(body, "stock"));
                        int stockMinimo = Integer.parseInt(extraerValorJson(body, "stockMinimo").isEmpty() ? "5" : extraerValorJson(body, "stockMinimo"));
                        String marca = extraerValorJson(body, "marca");
                        String descripcion = extraerValorJson(body, "descripcion");
                        
                        List<CategoriaProducto> cats = invDao.cargarCategorias();
                        CategoriaProducto catDefault = cats.isEmpty() ? new CategoriaProducto(1, "General", "General") : cats.get(0);
                        
                        Producto nuevo = new Producto(
                            0, nombre, descripcion.isEmpty() ? "Sin descripción" : descripcion,
                            marca.isEmpty() ? "Genitiva" : marca, precioCompra, precioVenta, stock, stockMinimo,
                            catDefault, TipoProducto.DAMA
                        );
                        
                        invDao.insertarProducto(nuevo);
                        if (authService != null) {
                            authService.cargarDatosDesdeBD();
                        }
                        
                        enviarRespuesta(exchange, 200, "{\"mensaje\":\"Producto agregado correctamente a la BD\"}");
                    } catch (IOException | NumberFormatException | SQLException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"Error al guardar en BD: " + e.getMessage() + "\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            // 2. Eliminar Producto (DELETE /api/productos/ID)[cite: 2]
            server.createContext("/api/productos/", exchange -> {
                configurarCORS(exchange);
                String path = exchange.getRequestURI().getPath();
                String[] partes = path.split("/");
                
                if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod()) && partes.length >= 4) {
                    try {
                        int id = Integer.parseInt(partes[3]);
                        invDao.eliminarProducto(id);
                        if (authService != null) authService.cargarDatosDesdeBD();
                        enviarRespuesta(exchange, 200, "{\"mensaje\":\"Producto eliminado con éxito de la BD\"}");
                    } catch (IOException | NumberFormatException | SQLException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"Error al eliminar: " + e.getMessage() + "\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            // 3. Registrar Venta, actualizar stock en MySQL, registrar en el sistema y generar Factura PDF (POST)[cite: 2]
            server.createContext("/api/ventas", (HttpExchange exchange) -> {
                configurarCORS(exchange);
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        
                        String cliente = extraerValorJson(body, "cliente");
                        int idProducto = Integer.parseInt(extraerValorJson(body, "idProducto"));
                        int cantidad = Integer.parseInt(extraerValorJson(body, "cantidad"));
                        
                        List<CategoriaProducto> cats = invDao.cargarCategorias();
                        List<Producto> listaInv = invDao.cargarProductos(cats);
                        Producto producto = listaInv.stream().filter(p -> p.getIdProducto() == idProducto).findFirst().orElse(null);
                        
                        if (producto == null || producto.getStock() < cantidad) {
                            enviarRespuesta(exchange, 400, "{\"error\":\"Stock insuficiente o producto no encontrado\"}");
                            return;
                        }
                        
                        int nuevoStock = producto.getStock() - cantidad;
                        invDao.actualizarStock(producto.getIdProducto(), nuevoStock);
                        if (authService != null) authService.cargarDatosDesdeBD();
                        
                        double total = producto.getPrecioVenta() * cantidad;
                        double gananciaUnitaria = producto.getPrecioVenta() - producto.getPrecioCompra();
                        double gananciaTotal = gananciaUnitaria * cantidad;

                        // Registrar formalmente la venta en AuthService para que aparezca en reportes de escritorio
                        int idVentaNuevo = (authService != null) ? authService.listarVentas().size() + 1 : 1;
                        Venta nuevaVenta = new Venta(idVentaNuevo, "Efectivo Móvil");
                        nuevaVenta.setCliente(cliente.isEmpty() ? "Cliente General" : cliente);
                        
                        DetalleVenta dv = new DetalleVenta(cantidad, producto.getPrecioVenta());
                        dv.setGanancia(gananciaTotal);
                        nuevaVenta.agregarDetalle(dv);
                        
                        if (authService != null) {
                            authService.agregarVenta(nuevaVenta);
                        }
                        
                        List<String[]> itemsFactura = new ArrayList<>();
                        itemsFactura.add(new String[]{
                            producto.getNombre(),
                            String.valueOf(cantidad),
                            String.valueOf(producto.getPrecioVenta()),
                            String.valueOf(total)
                        });
                        
                        Factura factura = new Factura(idVentaNuevo, "FAC-MOB-" + System.currentTimeMillis(), new Date(), total);
                        factura.exportarPDF("Factura_" + cliente.replaceAll(" ", "_") + ".pdf", cliente, itemsFactura);
                        
                        enviarRespuesta(exchange, 200, "{\"mensaje\":\"Venta procesada, stock actualizado y registrada en el sistema central\"}");
                    } catch (IOException | NumberFormatException | SQLException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"Error en venta: " + e.getMessage() + "\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            // 4. Listar Citas (GET) y Agendar Cita (POST) en /api/citas[cite: 2]
            server.createContext("/api/citas", (HttpExchange exchange) -> {
                configurarCORS(exchange);
                String method = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(method)) {
                    try {
                        List<Cita> citas = citaDao.cargarCitas();
                        StringBuilder json = new StringBuilder("[");
                        for (int i = 0; i < citas.size(); i++) {
                            Cita c = citas.get(i);
                            json.append(String.format(Locale.US,
                                "{\"idCita\":%d,\"cliente\":\"%s\",\"empleado\":\"%s\",\"servicio\":\"%s\",\"precioServicio\":%.2f,\"fecha\":\"%s\",\"hora\":\"%s\",\"estado\":\"%s\"}",
                                c.getIdCita(), c.getCliente(), c.getEmpleado(), c.getServicio(),
                                c.getPrecioServicio(), c.getFecha(), c.getHora(), c.getEstado() != null ? c.getEstado() : "PENDIENTE"
                            ));
                            if (i < citas.size() - 1) json.append(",");
                        }
                        json.append("]");
                        enviarRespuesta(exchange, 200, json.toString());
                    } catch (IOException | SQLException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                    }
                } else if ("POST".equalsIgnoreCase(method)) {
                    try {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        String cliente = extraerValorJson(body, "cliente");
                        String empleado = extraerValorJson(body, "empleado");
                        String servicio = extraerValorJson(body, "servicio");
                        double precio = Double.parseDouble(extraerValorJson(body, "precioServicio").isEmpty() ? "0" : extraerValorJson(body, "precioServicio"));
                        String fechaStr = extraerValorJson(body, "fecha");
                        String horaStr = extraerValorJson(body, "hora");

                        Cita nuevaCita = new Cita(0, 
                            cliente.isEmpty() ? "Cliente General" : cliente,
                            empleado.isEmpty() ? "Personal Asignado" : empleado,
                            servicio, precio,
                            LocalDate.parse(fechaStr),
                            LocalTime.parse(horaStr)
                        );
                        citaDao.guardarCita(nuevaCita);
                        if (authService != null) authService.cargarDatosDesdeBD();

                        enviarRespuesta(exchange, 200, "{\"mensaje\":\"Cita reservada y guardada con éxito en la BD\"}");
                    } catch (IOException | NumberFormatException | SQLException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"Error al agendar cita: " + e.getMessage() + "\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            // 5. Eliminar Cita (DELETE /api/citas/ID)[cite: 2]
            server.createContext("/api/citas/", exchange -> {
                configurarCORS(exchange);
                String path = exchange.getRequestURI().getPath();
                String[] partes = path.split("/");

                if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod()) && partes.length >= 4) {
                    try {
                        int idCita = Integer.parseInt(partes[3]);
                        citaDao.eliminarCita(idCita);
                        if (authService != null) authService.cargarDatosDesdeBD();
                        enviarRespuesta(exchange, 200, "{\"mensaje\":\"Cita eliminada/cancelada con éxito\"}");
                    } catch (IOException | NumberFormatException | SQLException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"Error al eliminar cita: " + e.getMessage() + "\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            // 6. Facturar Pago de Servicio Directo (POST /api/facturas/servicio)[cite: 2]
            server.createContext("/api/facturas/servicio", (HttpExchange exchange) -> {
                configurarCORS(exchange);
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        String cliente = extraerValorJson(body, "cliente");
                        String nombreServicio = extraerValorJson(body, "servicio");
                        double precio = Double.parseDouble(extraerValorJson(body, "precio").isEmpty() ? "0" : extraerValorJson(body, "precio"));

                        List<String[]> itemsFactura = new ArrayList<>();
                        itemsFactura.add(new String[]{nombreServicio, "1", String.format(Locale.US, "%.2f", precio), String.format(Locale.US, "%.2f", precio)});

                        Factura factura = new Factura(1, "FAC-SERV-" + System.currentTimeMillis(), new Date(), precio);
                        boolean exito = factura.exportarPDF("Factura_Servicio_" + cliente.replaceAll(" ", "_") + ".pdf", cliente, itemsFactura);

                        if (exito) {
                            enviarRespuesta(exchange, 200, "{\"mensaje\":\"Servicio cobrado y factura PDF generada exitosamente\"}");
                        } else {
                            enviarRespuesta(exchange, 500, "{\"error\":\"No se pudo generar el PDF de la factura\"}");
                        }
                    } catch (IOException | NumberFormatException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"Error al facturar servicio: " + e.getMessage() + "\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            // 7. Consultar Métricas del Negocio (GET)[cite: 2]
            server.createContext("/api/metricas", exchange -> {
                configurarCORS(exchange);
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try {
                        List<Cita> citas = citaDao.cargarCitas();
                        Metrica metrica = new Metrica(citas);
                        
                        String jsonRespuesta = String.format(Locale.US,
                            "{\"totalCitas\":%d,\"porcentajeCancelacion\":%.2f,\"promedioCitasPorDia\":%.2f,\"diaMasOcupado\":\"%s\",\"horaMasSolicitada\":\"%s\"}",
                            metrica.totalCitas(), metrica.porcentajeCancelacion(), metrica.promedioCitasPorDia(),
                            metrica.diaMasOcupado(), metrica.horaMasSolicitada()
                        );
                        enviarRespuesta(exchange, 200, jsonRespuesta);
                    } catch (IOException | SQLException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            // 8. Generar Reporte Excel (.xlsx) con los datos REALES acumulados del sistema (GET)[cite: 2]
            server.createContext("/api/reportes/excel", (HttpExchange exchange) -> {
                configurarCORS(exchange);
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try {
                        Reporte reporte = new Reporte(1, "Ventas_y_Ganancias", new Date(), new Date(), "EXCEL");
                        String[] encabezados = {"ID Venta", "Fecha", "Cliente", "Total (Q)", "Ganancia Neta (Q)"};
                        List<Object[]> datos = new ArrayList<>();
                        
                        if (authService != null && !authService.listarVentas().isEmpty()) {
                            for (Venta v : authService.listarVentas()) {
                                datos.add(new Object[]{
                                    v.getIdVenta(), 
                                    v.getFecha().toString(), 
                                    v.getCliente() != null ? v.getCliente() : "General", 
                                    v.getTotal(), 
                                    v.getGananciaVenta()
                                });
                            }
                        } else {
                            datos.add(new Object[]{0, new Date().toString(), "Sin ventas registradas", 0.00, 0.00});
                        }
                        
                        boolean exito = reporte.exportarExcelNativo("Reporte_NOVA_Financiero.xlsx", encabezados, datos);
                        if (exito) {
                            enviarRespuesta(exchange, 200, "{\"mensaje\":\"Reporte Excel generado con datos reales en la PC\"}");
                        } else {
                            enviarRespuesta(exchange, 500, "{\"error\":\"Error al generar el Excel\"}");
                        }
                    } catch (IOException e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("ServidorAPI nativo de NOVA corriendo en puerto 4567...");
        } catch (IOException e) {
            System.err.println("Error al iniciar el ServidorAPI: " + e.getMessage());
        }
    }

    private static String extraerValorJson(String json, String clave) {
        try {
            String buscar = "\"" + clave + "\":";
            int inicio = json.indexOf(buscar);
            if (inicio == -1) return "";
            inicio += buscar.length();
            
            while (inicio < json.length() && (json.charAt(inicio) == ' ' || json.charAt(inicio) == '\n' || json.charAt(inicio) == '\r')) {
                inicio++;
            }
            
            if (inicio >= json.length()) return "";
            if (json.charAt(inicio) == '\"') {
                inicio++;
                int fin = json.indexOf("\"", inicio);
                return json.substring(inicio, fin);
            } else {
                int fin = json.indexOf(",", inicio);
                if (fin == -1) fin = json.indexOf("}", inicio);
                if (fin == -1) fin = json.length();
                return json.substring(inicio, fin).trim();
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static void configurarCORS(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
        }
    }

    private static void enviarRespuesta(HttpExchange exchange, int statusCode, String respuesta) throws IOException {
        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}