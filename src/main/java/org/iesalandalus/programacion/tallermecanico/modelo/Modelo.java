package org.iesalandalus.programacion.tallermecanico.modelo;

import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.TipoTrabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Trabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface Modelo {
    void comenzar() throws Exception;

    void terminar();

    void insertar(Cliente cliente) throws Exception;

    void insertar(Vehiculo vehiculo) throws Exception;

    void insertar(Trabajo trabajo) throws Exception;

    Cliente buscar(Cliente cliente) throws IOException;

    Vehiculo buscar(Vehiculo vehiculo) throws IOException;

    Trabajo buscar(Trabajo trabajo) throws IOException;

    Cliente modificar(Cliente cliente, String nombre, String telefono) throws Exception;

    Trabajo anadirHoras(Trabajo trabajo, int horas) throws TallerMecanicoExcepcion, IOException;

    Trabajo anadirPrecioMaterial(Trabajo trabajo, float precioMaterial) throws TallerMecanicoExcepcion, IOException;

    Trabajo cerrar(Trabajo trabajo, LocalDate fechaFin) throws TallerMecanicoExcepcion, IOException;

    void borrar(Cliente cliente) throws Exception;

    void borrar(Vehiculo vehiculo) throws Exception;

    void borrar(Trabajo trabajo) throws Exception;

    List<Cliente> getClientes() throws IOException;

    List<Vehiculo> getVehiculos() throws IOException;

    List<Trabajo> getTrabajos() throws IOException;

    List<Trabajo> getTrabajos(Cliente cliente) throws IOException;

    List<Trabajo> getTrabajos(Vehiculo vehiculo) throws IOException;

    Map<TipoTrabajo, Integer> getEstadisticasMensuales(LocalDate mes) throws IOException;
}
