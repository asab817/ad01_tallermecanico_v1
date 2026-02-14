package org.iesalandalus.programacion.tallermecanico.modelo.negocio.mongodb;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.*;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ITrabajos;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;

public class Trabajos implements ITrabajos {

    private static final String COLECCION = "trabajos";
    private static final String CLIENTE_DNI = "cliente_dni";
    private static final String VEHICULO_MATRICULA = "vehiculo_matricula";
    private static final String FECHA_INICIO = "fecha_inicio";
    private static final String FECHA_FIN = "fecha_fin";
    private static final String TIPO = "tipo";
    private static final String HORAS = "horas";
    private static final String PRECIO_MATERIAL = "precio_material";

    private static Trabajos instancia;
    private MongoCollection<Document> coleccionTrabajos;

    private Trabajos() {
        // Singleton
    }

    public static Trabajos getInstancia() {
        if (instancia == null) {
            instancia = new Trabajos();
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        coleccionTrabajos = MongoDb.getBD().getCollection(COLECCION);
    }

    @Override
    public void terminar() {
        MongoDb.cerrarConexion();
    }

    @Override
    public List<Trabajo> get() {
        List<Trabajo> trabajos = new ArrayList<>();
        for (Document doc : coleccionTrabajos.find()) {
            Trabajo t = getTrabajo(doc);
            if (t != null) trabajos.add(t);
        }
        return trabajos;
    }

    @Override
    public List<Trabajo> get(Cliente cliente) {
        List<Trabajo> trabajos = new ArrayList<>();
        for (Document doc : coleccionTrabajos.find(eq(CLIENTE_DNI, cliente.getDni()))) {
            Trabajo t = getTrabajo(doc);
            if (t != null) trabajos.add(t);
        }
        return trabajos;
    }

    @Override
    public List<Trabajo> get(Vehiculo vehiculo) {
        List<Trabajo> trabajos = new ArrayList<>();
        for (Document doc : coleccionTrabajos.find(eq(VEHICULO_MATRICULA, vehiculo.matricula()))) {
            Trabajo t = getTrabajo(doc);
            if (t != null) trabajos.add(t);
        }
        return trabajos;
    }

    @Override
    public void insertar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        if (trabajo == null) throw new NullPointerException("El trabajo no puede ser nulo.");
        if (buscar(trabajo) != null) throw new TallerMecanicoExcepcion("El trabajo ya existe.");

        coleccionTrabajos.insertOne(getDocumento(trabajo));
    }

    @Override
    public Trabajo anadirHoras(Trabajo trabajo, int horas) throws TallerMecanicoExcepcion {
        if (trabajo == null) throw new NullPointerException("El trabajo no puede ser nulo.");

        Trabajo t = buscar(trabajo);
        if (t == null) throw new TallerMecanicoExcepcion("El trabajo no existe.");

        t.anadirHoras(horas); // Validamos lógica de dominio

        // Actualizamos en MongoDB incrementando las horas
        coleccionTrabajos.updateOne(getCriterioBusqueda(trabajo), Updates.inc(HORAS, horas));
        return buscar(trabajo);
    }

    @Override
    public Trabajo anadirPrecioMaterial(Trabajo trabajo, float precio) throws TallerMecanicoExcepcion {
        if (trabajo == null) throw new NullPointerException("El trabajo no puede ser nulo.");

        Trabajo t = buscar(trabajo);
        if (t == null) throw new TallerMecanicoExcepcion("El trabajo no existe.");
        if (!(t instanceof Mecanico)) throw new TallerMecanicoExcepcion("El trabajo no es de tipo mecánico.");

        ((Mecanico)t).anadirPrecioMaterial(precio); // Validación dominio

        // Actualizamos en MongoDB
        coleccionTrabajos.updateOne(getCriterioBusqueda(trabajo), Updates.inc(PRECIO_MATERIAL, precio));
        return buscar(trabajo);
    }

    @Override
    public Trabajo cerrar(Trabajo trabajo, LocalDate fechaFin) throws TallerMecanicoExcepcion {
        if (trabajo == null) throw new NullPointerException("El trabajo no puede ser nulo.");

        Trabajo t = buscar(trabajo);
        if (t == null) throw new TallerMecanicoExcepcion("El trabajo no existe.");

        t.cerrar(fechaFin); // Validación dominio

        coleccionTrabajos.updateOne(getCriterioBusqueda(trabajo), Updates.set(FECHA_FIN, dateParaMongo(fechaFin)));
        return buscar(trabajo);
    }

    @Override
    public Trabajo buscar(Trabajo trabajo) {
        if (trabajo == null) throw new NullPointerException("El trabajo no puede ser nulo.");
        Document doc = coleccionTrabajos.find(getCriterioBusqueda(trabajo)).first();
        return getTrabajo(doc);
    }

    @Override
    public void borrar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        if (trabajo == null) throw new NullPointerException("El trabajo no puede ser nulo.");
        if (buscar(trabajo) == null) throw new TallerMecanicoExcepcion("El trabajo no existe.");

        coleccionTrabajos.deleteOne(getCriterioBusqueda(trabajo));
    }

    @Override
    public Map<TipoTrabajo, Integer> getEstadisticasMensuales(LocalDate mes) {
        // Método opcional para estadísticas
        return null;
    }

    // --- MÉTODOS AUXILIARES ---

    private Bson getCriterioBusqueda(Trabajo trabajo) {
        // Clave primaria compuesta: Matrícula + Fecha Inicio
        return and(
                eq(VEHICULO_MATRICULA, trabajo.getVehiculo().matricula()),
                eq(FECHA_INICIO, dateParaMongo(trabajo.getFechaInicio()))
        );
    }

    private Trabajo getTrabajo(Document doc) {
        if (doc == null) return null;

        String dni = doc.getString(CLIENTE_DNI);
        String matricula = doc.getString(VEHICULO_MATRICULA);
        LocalDate fechaInicio = dateParaJava(doc.getDate(FECHA_INICIO));

        // Recuperamos Cliente y Vehículo REALES de sus colecciones
        Cliente c = Clientes.getInstancia().buscar(Cliente.get(dni));
        Vehiculo v = Vehiculos.getInstancia().buscar(Vehiculo.get(matricula));

        if (c == null || v == null) return null; // Integridad referencial

        String tipo = doc.getString(TIPO);
        Trabajo trabajo;

        if ("REVISION".equals(tipo)) {
            trabajo = new Revision(c, v, fechaInicio);
        } else {
            trabajo = new Mecanico(c, v, fechaInicio);
            Double precio = doc.getDouble(PRECIO_MATERIAL);
            if (precio != null) {
                try { ((Mecanico)trabajo).anadirPrecioMaterial(precio.floatValue()); } catch (Exception e) {}
            }
        }

        Integer horas = doc.getInteger(HORAS);
        if (horas != null && horas > 0) {
            try { trabajo.anadirHoras(horas); } catch (Exception e) {}
        }

        Date fechaFin = doc.getDate(FECHA_FIN);
        if (fechaFin != null) {
            try { trabajo.cerrar(dateParaJava(fechaFin)); } catch (Exception e) {}
        }

        return trabajo;
    }

    private Document getDocumento(Trabajo trabajo) {
        Document doc = new Document()
                .append(CLIENTE_DNI, trabajo.getCliente().getDni())
                .append(VEHICULO_MATRICULA, trabajo.getVehiculo().matricula())
                .append(FECHA_INICIO, dateParaMongo(trabajo.getFechaInicio()))
                .append(TIPO, (trabajo instanceof Revision) ? "REVISION" : "MECANICO")
                .append(HORAS, trabajo.getHoras());

        if (trabajo instanceof Mecanico) {
            doc.append(PRECIO_MATERIAL, ((Mecanico)trabajo).getPrecioMaterial());
        }

        if (trabajo.getFechaFin() != null) {
            doc.append(FECHA_FIN, dateParaMongo(trabajo.getFechaFin()));
        }
        return doc;
    }

    // Conversión LocalDate <-> java.util.Date (MongoDB usa Date)
    private Date dateParaMongo(LocalDate lDate) {
        return Date.from(lDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate dateParaJava(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}