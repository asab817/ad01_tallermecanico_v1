package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.xml;


// Asumo que estas interfaces están en ficheros.json o negocio
import negocio.IFuenteDatos;
import ficheros.json.Clientes;
import ficheros.json.Vehiculos;
import ficheros.json.Trabajos;
import ficheros.json.IClientes;
import ficheros.json.IVehiculos;
import ficheros.json.ITrabajos;

/**
 * Implementa el patrón Fábrica (Factory) para la persistencia mediante ficheros JSON.
 * Devuelve las instancias Singleton de las clases de acceso a datos.
 */
public class FuenteDatosFicherosJSON implements IFuenteDatos {

    /**
     * Devuelve una instancia Singleton de la clase Clientes.
     * @return Instancia de IClientes.
     */
    @Override
    public IClientes crearClientes() {
        return Clientes.getInstancia();
    }

    /**
     * Devuelve una instancia Singleton de la clase Vehiculos.
     * @return Instancia de IVehiculos.
     */
    @Override
    public IVehiculos crearVehiculos() {
        return Vehiculos.getInstancia();
    }

    /**
     * Devuelve una instancia Singleton de la clase Trabajos.
     * @return Instancia de ITrabajos.
     */
    @Override
    public ITrabajos crearTrabajos() {
        return Trabajos.getInstancia();
    }
}