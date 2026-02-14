package org.iesalandalus.programacion.tallermecanico.modelo.negocio;

import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;

import java.io.IOException; // Importación necesaria
import java.util.List;

public interface IVehiculos {

    void comenzar() throws Exception;

    void terminar();

    List<Vehiculo> get() throws IOException;

    void insertar(Vehiculo vehiculo) throws TallerMecanicoExcepcion, IOException, Exception;

    Vehiculo buscar(Vehiculo vehiculo) throws IOException;

    void borrar(Vehiculo vehiculo) throws TallerMecanicoExcepcion, IOException, Exception;
}