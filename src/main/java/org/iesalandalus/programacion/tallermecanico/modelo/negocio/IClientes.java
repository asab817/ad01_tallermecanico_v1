package org.iesalandalus.programacion.tallermecanico.modelo.negocio;

import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;

import java.io.IOException; // ¡Importación necesaria!
import java.util.List;

public interface IClientes {

    void comenzar();

    void terminar();

    List<Cliente> get() throws IOException;

    void insertar(Cliente cliente) throws TallerMecanicoExcepcion, IOException;

    Cliente modificar(Cliente cliente, String nombre, String telefono) throws TallerMecanicoExcepcion, IOException;

    Cliente buscar(Cliente cliente) throws IOException;

    void borrar(Cliente cliente) throws TallerMecanicoExcepcion, IOException;
}
