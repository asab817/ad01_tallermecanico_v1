package org.iesalandalus.programacion.tallermecanico.modelo.cascada;

import org.iesalandalus.programacion.tallermecanico.modelo.Modelo;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.*;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModeloCascada implements Modelo {
    private IClientes clientes;
    private IVehiculos vehiculos;
    private ITrabajos trabajos;

    public ModeloCascada(FabricaFuenteDatos fabricaFuenteDatos) {
        Objects.requireNonNull(fabricaFuenteDatos, "La factoría de la fuente de datos no puede ser nula.");
        IFuenteDatos fuenteDatos = fabricaFuenteDatos.crear();
        clientes = fuenteDatos.crearClientes();
        vehiculos = fuenteDatos.crearVehiculos();
        trabajos = fuenteDatos.crearTrabajos();
    }

    @Override
    public void comenzar() {
        clientes.comenzar();
        vehiculos.comenzar();
        trabajos.comenzar();
        System.out.println("Modelo comenzado.");
    }

    @Override
    public void terminar() {
        clientes.terminar();
        vehiculos.terminar();
        trabajos.terminar();
        System.out.println("Modelo terminado.");
    }

    @Override
    public void insertar(Cliente cliente) throws TallerMecanicoExcepcion {
        try {
            clientes.insertar(new Cliente(cliente));
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al insertar cliente: " + e.getMessage());
        }
    }

    @Override
    public void insertar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        try {
            vehiculos.insertar(vehiculo);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al insertar vehículo: " + e.getMessage());
        }
    }

    @Override
    public void insertar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        try {
            Cliente cliente = clientes.buscar(trabajo.getCliente());
            Vehiculo vehiculo = vehiculos.buscar(trabajo.getVehiculo());
            if (trabajo instanceof Revision) {
                trabajo = new Revision(cliente, vehiculo, trabajo.getFechaInicio());
            } else {
                trabajo = new Mecanico(cliente, vehiculo, trabajo.getFechaInicio());
            }
            trabajos.insertar(trabajo);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al insertar trabajo: " + e.getMessage());
        }
    }

    @Override
    public Cliente buscar(Cliente cliente) {
        try {
            cliente = Objects.requireNonNull(clientes.buscar(cliente), "No existe un cliente igual.");
            return new Cliente(cliente);
        } catch (IOException e) {
            throw new RuntimeException("Error de escritura/lectura al buscar cliente.", e);
        }
    }

    @Override
    public Vehiculo buscar(Vehiculo vehiculo) {
        try {
            vehiculo = Objects.requireNonNull(vehiculos.buscar(vehiculo), "No existe un vehículo igual.");
            return vehiculo;
        } catch (IOException e) {
            throw new RuntimeException("Error de escritura/lectura al buscar vehículo.", e);
        }
    }

    @Override
    public Trabajo buscar(Trabajo trabajo) {
        try {
            trabajo = Objects.requireNonNull(trabajos.buscar(trabajo), "No existe un trabajo igual.");
            return Trabajo.copiar(trabajo);
        } catch (IOException e) {
            throw new RuntimeException("Error de escritura/lectura al buscar trabajo.", e);
        }
    }

    @Override
    public Cliente modificar(Cliente cliente, String nombre, String telefono) throws TallerMecanicoExcepcion {
        try {
            return clientes.modificar(cliente, nombre, telefono);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al modificar cliente: " + e.getMessage());
        }
    }

    @Override
    public Trabajo anadirHoras(Trabajo trabajo, int horas) throws TallerMecanicoExcepcion {
        try {
            return trabajos.anadirHoras(trabajo, horas);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al añadir horas: " + e.getMessage());
        }
    }

    @Override
    public Trabajo anadirPrecioMaterial(Trabajo trabajo, float precioMaterial) throws TallerMecanicoExcepcion {
        try {
            return trabajos.anadirPrecioMaterial(trabajo, precioMaterial);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al añadir precio material: " + e.getMessage());
        }
    }

    @Override
    public Trabajo cerrar(Trabajo trabajo, LocalDate fechaFin) throws TallerMecanicoExcepcion {
        try {
            return trabajos.cerrar(trabajo, fechaFin);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al cerrar trabajo: " + e.getMessage());
        }
    }

    @Override
    public void borrar(Cliente cliente) throws TallerMecanicoExcepcion {
        try {
            List<Trabajo> trabajosCliente = trabajos.get(cliente);
            for (Trabajo trabajo : trabajosCliente) {
                trabajos.borrar(trabajo);
            }
            clientes.borrar(cliente);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al borrar cliente: " + e.getMessage());
        }
    }

    @Override
    public void borrar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        try {
            List<Trabajo> trabajosVehiculo = trabajos.get(vehiculo);
            for (Trabajo trabajo : trabajosVehiculo) {
                trabajos.borrar(trabajo);
            }
            vehiculos.borrar(vehiculo);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al borrar vehículo: " + e.getMessage());
        }
    }

    @Override
    public void borrar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        try {
            trabajos.borrar(trabajo);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("Error de escritura/lectura al borrar trabajo: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> getClientes() {
        try {
            List<Cliente> copiaClientes = new ArrayList<>();
            for (Cliente cliente : clientes.get()) {
                copiaClientes.add(new Cliente(cliente));
            }
            return copiaClientes;
        } catch (IOException e) {
            throw new RuntimeException("Error de escritura/lectura al obtener clientes.", e);
        }
    }

    @Override
    public List<Vehiculo> getVehiculos() {
        try {
            return vehiculos.get();
        } catch (IOException e) {
            throw new RuntimeException("Error de escritura/lectura al obtener vehículos.", e);
        }
    }

    @Override
    public List<Trabajo> getTrabajos() {
        try {
            List<Trabajo> copiaTrabajos = new ArrayList<>();
            for (Trabajo trabajo : trabajos.get()) {
                copiaTrabajos.add(Trabajo.copiar(trabajo));
            }
            return copiaTrabajos;
        } catch (IOException e) {
            throw new RuntimeException("Error de escritura/lectura al obtener trabajos.", e);
        }
    }

    @Override
    public List<Trabajo> getTrabajos(Cliente cliente) {
        try {
            List<Trabajo> trabajosCliente = new ArrayList<>();
            for (Trabajo trabajo : trabajos.get(cliente)) {
                trabajosCliente.add(Trabajo.copiar(trabajo));
            }
            return trabajosCliente;
        } catch (IOException e) {
            throw new RuntimeException("Error de escritura/lectura al obtener trabajos del cliente.", e);
        }
    }

    @Override
    public List<Trabajo> getTrabajos(Vehiculo vehiculo) {
        try {
            List<Trabajo> trabajosCliente = new ArrayList<>();
            for (Trabajo trabajo : trabajos.get(vehiculo)) {
                trabajosCliente.add(Trabajo.copiar(trabajo));
            }
            return trabajosCliente;
        } catch (IOException e) {
            throw new RuntimeException("Error de escritura/lectura al obtener trabajos del vehículo.", e);
        }
    }

    @Override
    public Map<TipoTrabajo, Integer> getEstadisticasMensuales(LocalDate mes) {
        try {
            return trabajos.getEstadisticasMensuales(mes);
        } catch (IOException e) {
            throw new RuntimeException("Error de escritura/lectura al obtener estadísticas.", e);
        }
    }
}