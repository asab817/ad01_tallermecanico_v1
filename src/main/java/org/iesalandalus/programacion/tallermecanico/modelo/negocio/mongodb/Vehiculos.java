package org.iesalandalus.programacion.tallermecanico.modelo.negocio.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IVehiculos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vehiculos implements IVehiculos {
    private static final String COLECCION = "vehiculos";
    private static final String MARCA = "marca";
    private static final String MODELO = "modelo";
    private static final String MATRICULA = "matricula";

    private MongoCollection<Document> coleccionVehiculos;
    private static Vehiculos instancia;


    private Vehiculos() {

    }

    public static Vehiculos getInstancia() {
        if (instancia == null) {
            instancia = new Vehiculos();
        }
        return instancia;
    }



    @Override
    public void comenzar() {
        coleccionVehiculos = MongoDb.getBD().getCollection(COLECCION);
    }

    @Override
    public void terminar() {
        MongoDb.cerrarConexion();
    }




    private Vehiculo getVehiculo(Document documento) {
        Objects.requireNonNull(documento, "Error: Documento nulo");
        return new Vehiculo(
                documento.getString(MARCA),
                documento.getString(MODELO),
                documento.getString(MATRICULA)
        );
    }


    private Document getDocumento(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "Error: Vehiculo nulo");
        return new Document()
                .append(MARCA, vehiculo.marca())
                .append(MODELO, vehiculo.modelo())
                .append(MATRICULA, vehiculo.matricula());
    }



    @Override
    public List<Vehiculo> get() {
        List<Vehiculo> listaVehiculos = new ArrayList<>();
        for (Document documento : coleccionVehiculos.find().sort(Sorts.ascending(MATRICULA))) {
            Vehiculo vehiculo = getVehiculo(documento);
            if (vehiculo != null) {
                listaVehiculos.add(vehiculo);
            }
        }
        return listaVehiculos;
    }

    @Override
    public void insertar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(vehiculo, "Error: Vehiculo nulo");

        if (buscar(vehiculo) != null) {
            throw new TallerMecanicoExcepcion("El vehículo ya existe.");
        }

        coleccionVehiculos.insertOne(getDocumento(vehiculo));
    }

    @Override
    public Vehiculo buscar(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "Error: Vehiculo nulo");

        Document documento = coleccionVehiculos.find(Filters.eq(MATRICULA, vehiculo.matricula())).first();
        if (documento == null) {
            return null;
        }
        return getVehiculo(documento);
    }

    @Override
    public void borrar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(vehiculo, "Error: Vehiculo nulo");
        if (buscar(vehiculo) == null) {
            throw new TallerMecanicoExcepcion("El vehículo a borrar no existe.");
        }

        coleccionVehiculos.deleteOne(Filters.eq(MATRICULA, vehiculo.matricula()));
    }
}

