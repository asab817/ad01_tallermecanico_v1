package org.iesalandalus.programacion.tallermecanico.modelo.negocio.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDb {
    // 1. Pon aquí SOLO la parte del servidor (lo que va después del @)
    private static final String SERVIDOR = "@cluster0.rj6vuoj.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

    private static final int PUERTO = 27017;
    private static final String BD = "tallerMecanico";
    private static final String USUARIO = "taller";
    private static final String CONTRASENA = "taller-2025";

    private static MongoClient conexion = null;

    public MongoDb() {

    }
    public static MongoDatabase getBD() {
        if (conexion == null) {
            establecerConexion();
        }
        return conexion.getDatabase(BD);
    }
    private static void establecerConexion() {
        try {


            String uri = "mongodb+srv://" + USUARIO + ":" + CONTRASENA + SERVIDOR;
            conexion = MongoClients.create(uri);

            System.out.println(">> Conexión establecida con éxito.");
        } catch (Exception e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println(">> Conexión cerrada.");
            } catch (Exception e) {
                System.err.println("Error al cerrar la conexion con MongoDB: " + e.getMessage());
            }

        }

    }

}
