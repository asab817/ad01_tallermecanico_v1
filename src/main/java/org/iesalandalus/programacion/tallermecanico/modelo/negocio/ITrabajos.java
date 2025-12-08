package org.iesalandalus.programacion.tallermecanico.modelo.negocio;

import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.*;

import java.io.IOException; // ¡IMPORTACIÓN DE IOEXCEPTION AÑADIDA!
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ITrabajos {
    void comenzar();

    void terminar();

    List<Trabajo> get() throws IOException;

    List<Trabajo> get(Cliente cliente) throws IOException;

    List<Trabajo> get(Vehiculo vehiculo) throws IOException;

    Map<TipoTrabajo, Integer> getEstadisticasMensuales(LocalDate mes) throws IOException;

    void insertar(Trabajo trabajo) throws TallerMecanicoExcepcion, IOException;

    Trabajo anadirHoras(Trabajo trabajo, int horas) throws TallerMecanicoExcepcion, IOException;

    Trabajo anadirPrecioMaterial(Trabajo trabajo, float precioMaterial) throws TallerMecanicoExcepcion, IOException;

    Trabajo cerrar(Trabajo trabajo, LocalDate fechaFin) throws TallerMecanicoExcepcion, IOException;

    Trabajo buscar(Trabajo trabajo) throws IOException;

    void borrar(Trabajo trabajo) throws TallerMecanicoExcepcion, IOException;
}