package org.iesalandalus.programacion.tallermecanico.modelo.negocio.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IClientes;

public class Clientes implements IClientes {
    private static Clientes instancia = null;
    private Connection conexion;

    private Clientes() {}

    public static Clientes getInstancia() {
        if (instancia == null) instancia = new Clientes();
        return instancia;
    }

    @Override
    public void comenzar() throws SQLException { this.conexion = MySQL.establecerConexion(); }
    @Override
    public void terminar() { MySQL.cerrarConexion(); }

    @Override
    public List<Cliente> get() {
        List<Cliente> lista = new ArrayList<>();
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clientes")) {
            while (rs.next()) {
                lista.add(new Cliente(rs.getString("nombre"), rs.getString("dni"), rs.getString("telefono")));
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
        return lista;
    }

    @Override
    public void insertar(Cliente cliente) throws TallerMecanicoExcepcion {
        try (PreparedStatement pstmt = conexion.prepareStatement("INSERT INTO clientes VALUES (?, ?, ?)")) {
            pstmt.setString(1, cliente.getDni());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new TallerMecanicoExcepcion("Error al insertar cliente (posible duplicado).");
        }
    }

    @Override
    public Cliente modificar(Cliente cliente, String nombre, String telefono) throws TallerMecanicoExcepcion {
        try (PreparedStatement pstmt = conexion.prepareStatement("UPDATE clientes SET nombre=?, telefono=? WHERE dni=?")) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, cliente.getDni());
            if (pstmt.executeUpdate() == 0) throw new TallerMecanicoExcepcion("Cliente no encontrado.");
            return new Cliente(nombre, cliente.getDni(), telefono);
        } catch (SQLException e) { throw new TallerMecanicoExcepcion("Error al modificar cliente."); }
    }

    @Override
    public Cliente buscar(Cliente cliente) {
        try (PreparedStatement pstmt = conexion.prepareStatement("SELECT * FROM clientes WHERE dni=?")) {
            pstmt.setString(1, cliente.getDni());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return new Cliente(rs.getString("nombre"), rs.getString("dni"), rs.getString("telefono"));
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
        return null;
    }

    @Override
    public void borrar(Cliente cliente) throws TallerMecanicoExcepcion {
        try (PreparedStatement pstmt = conexion.prepareStatement("DELETE FROM clientes WHERE dni=?")) {
            pstmt.setString(1, cliente.getDni());
            if (pstmt.executeUpdate() == 0) throw new TallerMecanicoExcepcion("El cliente no existe.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new TallerMecanicoExcepcion("No se puede borrar el cliente porque tiene trabajos o vehículos asociados.");
            }
            throw new TallerMecanicoExcepcion("Error al borrar cliente.");
        }
    }
}