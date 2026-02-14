package org.iesalandalus.programacion.tallermecanico.modelo.negocio.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    // Nuevos datos de conexión AWS
    private static final String HOST = "3.230.120.47";
    private static final String PUERTO = "3306";
    private static final String BD = "asab_db";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "Anouar26_";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BD +
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";


    private static Connection conexion = null;

    private MySQL() {}

    public static Connection establecerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}