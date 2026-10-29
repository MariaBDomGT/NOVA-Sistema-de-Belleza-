package com.belleza.servicio;

import com.belleza.modelo.Servicio;
import java.util.ArrayList;
import java.util.List;

public class GestorServicios {
    private final List<Servicio> catalogoServicios = new ArrayList<>();

    public boolean registrarServicio(Servicio servicio) {
        if (buscarPorId(servicio.getId()) != null) return false;
        catalogoServicios.add(servicio);
        return true;
    }

    public Servicio buscarPorId(String id) {
        for (Servicio s : catalogoServicios) {
            if (s.getId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }

    public boolean cambiarEstadoDisponibilidad(String id, boolean estado) {
        Servicio s = buscarPorId(id);
        if (s != null) {
            s.setActivo(estado);
            return true;
        }
        return false;
    }

    public List<Servicio> listarTodos() { return new ArrayList<>(catalogoServicios); }
    public List<Servicio> listarDisponibles() {
        List<Servicio> disp = new ArrayList<>();
        for (Servicio s : catalogoServicios) if (s.isActivo()) disp.add(s);
        return disp;
    }
}