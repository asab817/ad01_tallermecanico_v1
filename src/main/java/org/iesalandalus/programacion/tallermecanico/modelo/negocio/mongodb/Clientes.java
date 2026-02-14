package org.iesalandalus.programacion.tallermecanico.modelo.negocio.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IClientes;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Updates.set;

public class Clientes implements IClientes {
    private static final String COLECCION = "clientes";
    private static final String NOMBRE = "nombre";
    private static final String DNI = "dni";
    private static final String TELEFONO = "telefono";
    private MongoCollection<Document> coleccionClientes;
    private static Clientes instancia;

    private Clientes() {}

    public static Clientes getInstancia() {
        if (instancia == null) instancia = new Clientes();
        return instancia;
    }

    @Override
    public void comenzar() {
        coleccionClientes = MongoDb.getBD().getCollection(COLECCION);
    }

    @Override
    public void terminar() {
        MongoDb.cerrarConexion();
    }
    private Cliente getCliente(Document documento) {
        Objects.requireNonNull(documento, "Error: Documento nulo");
        String nombre = documento.get(NOMBRE).toString();
        String dni = documento.get(DNI).toString();
        String telefono = documento.get(TELEFONO).toString();
        return new Cliente(nombre, dni, telefono);

    }
    private Document getDocument(Cliente cliente) {
        Objects.requireNonNull(cliente, "Error: Cliente nulo");
        Document document = new Document();
        document.append(NOMBRE, cliente.getNombre());
        document.append(DNI, cliente.getDni());
        document.append(TELEFONO, cliente.getTelefono());
        return document;
    }

    @Override
    public List<Cliente> get() {
        List<Cliente> clientes = new ArrayList<>();
        for (Document doc : coleccionClientes.find().sort(Sorts.ascending(DNI))) {
            Cliente cliente = getCliente(doc);
            if (cliente != null) {
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    @Override
    public void insertar(Cliente cliente) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(cliente, "Error: Cliente nulo");

        coleccionClientes.insertOne(getDocument(cliente));
        System.out.println(">> Cliente insertado correctamente.");
    }

    @Override
    public Cliente modificar(Cliente cliente, String nombre, String telefono) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(cliente, "Error: Cliente nulo");
        var resultado = coleccionClientes.updateOne(Filters.eq("dni", cliente.getDni()), Updates.combine(set(NOMBRE, nombre), set(TELEFONO, telefono)));
        return cliente;
    }

    @Override
    public Cliente buscar(Cliente cliente) {
        Objects.requireNonNull(cliente, "Error: Cliente nulo");
        Document doc = coleccionClientes.find(Filters.eq("dni", cliente.getDni())).first();
        if (doc != null) {
            System.out.println("Encontrado: " + doc.toJson());
        } else {
            System.out.println("No encontrado: " + cliente.getDni());
        }
        return getCliente(doc);
    }

    @Override
    public void borrar(Cliente cliente) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(cliente, "Error: Cliente nulo");
        coleccionClientes.deleteOne(Filters.eq("dni", cliente.getDni()));
        System.out.println(">> Cliente borrado correctamente.");

    }
}
