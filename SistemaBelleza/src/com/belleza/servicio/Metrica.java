package com.belleza.servicio;

import com.belleza.modelo.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Metrica {
    private final Collection<Cita> citas;

    public Metrica(Collection<Cita> citas) {
        this.citas = citas;
    }

    public int totalCitas() { return citas.size(); }

    public Map<String, Integer> citasPorEstado() {
        Map<String, Integer> conteo = new HashMap<>();
        for (Cita c : citas) {
            String estado = c.getEstado() != null ? c.getEstado() : "DESCONOCIDO";
            conteo.put(estado, conteo.getOrDefault(estado, 0) + 1);
        }
        return conteo;
    }

    public Map<LocalDate, Integer> citasPorFecha() {
        Map<LocalDate, Integer> conteo = new TreeMap<>();
        for (Cita c : citas) {
            if (c.getFecha() != null) conteo.put((LocalDate) c.getFecha(), conteo.getOrDefault(c.getFecha(), 0) + 1);
        }
        return conteo;
    }

    public Map<LocalTime, Integer> citasPorHora() {
        Map<LocalTime, Integer> conteo = new TreeMap<>();
        for (Cita c : citas) {
            if (c.getHora() != null) conteo.put((LocalTime) c.getHora(), conteo.getOrDefault(c.getHora(), 0) + 1);
        }
        return conteo;
    }

    public String diaMasOcupado() {
        var porFecha = citasPorFecha();
        LocalDate top = null;
        int max = 0;
        for (var e : porFecha.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                top = e.getKey();
            }
        }
        return top == null ? "N/A" : top + " (" + max + " citas)";
    }

    public String horaMasSolicitada() {
        var porHora = citasPorHora();
        LocalTime top = null;
        int max = 0;
        for (var e : porHora.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                top = e.getKey();
            }
        }
        return top == null ? "N/A" : top + " (" + max + " citas)";
    }

    public double porcentajeCancelacion() {
        if (citas.isEmpty()) return 0;
        int canceladas = citasPorEstado().getOrDefault("CANCELADA", 0);
        return (canceladas * 100.0) / citas.size();
    }

    public double promedioCitasPorDia() {
        var porFecha = citasPorFecha();
        if (porFecha.isEmpty()) return 0;
        return (double) citas.size() / porFecha.size();
    }
}