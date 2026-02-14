package org.iesalandalus.programacion.tallermecanico.modelo.negocio.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IVehiculos;

public class Vehiculos implements IVehiculos {
    private static Vehiculos instancia = null;
    private Connection conexion;

    private Vehiculos() {}
    public static Vehiculos getInstancia() {
        if (instancia == null) instancia = new Vehiculos();
        return instancia;
    }

    @Override public void comenzar() throws SQLException { this.conexion = MySQL.establecerConexion(); }
    @Override public void terminar() { MySQL.cerrarConexion(); }

    @Override
    public List<Vehiculo> get() {
        List<Vehiculo> lista = new ArrayList<>();
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM vehiculos")) {
            while (rs.next()) {
                lista.add(new Vehiculo(rs.getString("marca"), rs.getString("modelo"), rs.getString("matricula")));
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
        return lista;
    }

    @Override
    public void insertar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        try (PreparedStatement pstmt = conexion.prepareStatement("INSERT INTO vehiculos (matricula, marca, modelo) VALUES (?, ?, ?)")) {
            pstmt.setString(1, vehiculo.matricula()); // Record
            pstmt.setString(2, vehiculo.marca());
            pstmt.setString(3, vehiculo.modelo());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new TallerMecanicoExcepcion("Error al insertar vehículo (posible duplicado).");
        }
    }

    @Override
    public Vehiculo buscar(Vehiculo vehiculo) {
        try (PreparedStatement pstmt = conexion.prepareStatement("SELECT * FROM vehiculos WHERE matricula=?")) {
            pstmt.setString(1, vehiculo.matricula());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return new Vehiculo(rs.getString("marca"), rs.getString("modelo"), rs.getString("matricula"));
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
        return null;
    }

    @Override
    public void borrar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        try (PreparedStatement pstmt = conexion.prepareStatement("DELETE FROM vehiculos WHERE matricula=?")) {
            pstmt.setString(1, vehiculo.matricula());
            if (pstmt.executeUpdate() == 0) throw new TallerMecanicoExcepcion("El vehículo no existe.");
        } catch (SQLException e) {
            // REQUISITO CUMPLIDO: Bloqueo de borrado por trabajos existentes
            if (e.getErrorCode() == 1451) {
                throw new TallerMecanicoExcepcion("No se puede borrar el vehículo porque tiene trabajos realizados.");
            }
            throw new TallerMecanicoExcepcion("Error al borrar vehículo.");
        }
    }
}