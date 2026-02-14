
package org.iesalandalus.programacion.tallermecanico.modelo.negocio.mysql;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.*;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ITrabajos;

public class Trabajos implements ITrabajos {
    private static Trabajos instancia = null;
    private Connection conexion;
    private Trabajos() {}
    public static Trabajos getInstancia() { if(instancia==null) instancia=new Trabajos(); return instancia; }

    @Override public void comenzar() throws SQLException { this.conexion = MySQL.establecerConexion(); }
    @Override public void terminar() { MySQL.cerrarConexion(); }

    @Override
    public List<Trabajo> get() { return ejecutarConsulta("SELECT * FROM trabajos"); }

    @Override
    public List<Trabajo> get(Cliente cliente) { return ejecutarConsulta("SELECT * FROM trabajos WHERE cliente_dni = '" + cliente.getDni() + "'"); }

    @Override
    public List<Trabajo> get(Vehiculo vehiculo) { return ejecutarConsulta("SELECT * FROM trabajos WHERE vehiculo_matricula = '" + vehiculo.matricula() + "'"); }

    private List<Trabajo> ejecutarConsulta(String sql) {
        List<Trabajo> lista = new ArrayList<>();
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Trabajo t = reconstruirTrabajo(rs);
                if(t!=null) lista.add(t);
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
        return lista;
    }

    @Override
    public void insertar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        String sql = "INSERT INTO trabajos (cliente_dni, vehiculo_matricula, fecha_inicio, estado, tipo_trabajo, horas, precio_material) VALUES (?, ?, ?, 'ABIERTO', ?, 0, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, trabajo.getCliente().getDni());
            pstmt.setString(2, trabajo.getVehiculo().matricula());
            pstmt.setDate(3, Date.valueOf(trabajo.getFechaInicio()));

            String tipo = (trabajo instanceof Revision) ? "REVISION" : "MECANICO";
            float material = (trabajo instanceof Mecanico) ? ((Mecanico)trabajo).getPrecioMaterial() : 0; // Asumiendo getter visible

            pstmt.setString(4, tipo);
            pstmt.setFloat(5, material);
            pstmt.executeUpdate();
        } catch (SQLException e) { throw new TallerMecanicoExcepcion("Error al insertar trabajo."); }
    }

    @Override
    public Trabajo anadirHoras(Trabajo trabajo, int horas) throws TallerMecanicoExcepcion {
        try { trabajo.anadirHoras(horas); } catch(Exception e) { throw new TallerMecanicoExcepcion(e.getMessage()); }

        String sql = "UPDATE trabajos SET horas = horas + ? WHERE vehiculo_matricula = ? AND fecha_inicio = ?";
        try(PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, horas);
            pstmt.setString(2, trabajo.getVehiculo().matricula());
            pstmt.setDate(3, Date.valueOf(trabajo.getFechaInicio()));
            if(pstmt.executeUpdate() == 0) throw new TallerMecanicoExcepcion("Trabajo no encontrado.");
            return buscar(trabajo);
        } catch(SQLException e) { throw new TallerMecanicoExcepcion("Error BD."); }
    }

    @Override
    public Trabajo anadirPrecioMaterial(Trabajo trabajo, float precio) throws TallerMecanicoExcepcion {
        if(!(trabajo instanceof Mecanico)) throw new TallerMecanicoExcepcion("Solo para Mecánicos.");
        if(trabajo.estaCerrado()) throw new TallerMecanicoExcepcion("Trabajo cerrado.");

        String sql = "UPDATE trabajos SET precio_material = precio_material + ? WHERE vehiculo_matricula = ? AND fecha_inicio = ?";
        try(PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setFloat(1, precio);
            pstmt.setString(2, trabajo.getVehiculo().matricula());
            pstmt.setDate(3, Date.valueOf(trabajo.getFechaInicio()));
            if(pstmt.executeUpdate() == 0) throw new TallerMecanicoExcepcion("Trabajo no encontrado.");
            return buscar(trabajo);
        } catch(SQLException e) { throw new TallerMecanicoExcepcion("Error BD."); }
    }

    @Override
    public Trabajo cerrar(Trabajo trabajo, LocalDate fechaFin) throws TallerMecanicoExcepcion {
        try { trabajo.cerrar(fechaFin); } catch(Exception e) { throw new TallerMecanicoExcepcion(e.getMessage()); }
        String sql = "UPDATE trabajos SET fecha_fin = ?, estado = 'CERRADO' WHERE vehiculo_matricula = ? AND fecha_inicio = ?";
        try(PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(fechaFin));
            pstmt.setString(2, trabajo.getVehiculo().matricula());
            pstmt.setDate(3, Date.valueOf(trabajo.getFechaInicio()));
            if(pstmt.executeUpdate() == 0) throw new TallerMecanicoExcepcion("Trabajo no encontrado.");
            return buscar(trabajo);
        } catch(SQLException e) { throw new TallerMecanicoExcepcion("Error BD."); }
    }

    @Override
    public Trabajo buscar(Trabajo trabajo) {
        String sql = "SELECT * FROM trabajos WHERE vehiculo_matricula = ? AND fecha_inicio = ?";
        try(PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, trabajo.getVehiculo().matricula());
            pstmt.setDate(2, Date.valueOf(trabajo.getFechaInicio()));
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) return reconstruirTrabajo(rs);
            }
        } catch(Exception e) {
            System.err.println("Error al buscar el trabajo: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void borrar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        String sql = "DELETE FROM trabajos WHERE vehiculo_matricula = ? AND fecha_inicio = ?";
        try(PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, trabajo.getVehiculo().matricula());
            pstmt.setDate(2, Date.valueOf(trabajo.getFechaInicio()));
            if(pstmt.executeUpdate() == 0) throw new TallerMecanicoExcepcion("El trabajo no existe.");
        } catch(SQLException e) { throw new TallerMecanicoExcepcion("Error al borrar."); }
    }

    @Override
    public Map<TipoTrabajo, Integer> getEstadisticasMensuales(LocalDate mes) {
        Map<TipoTrabajo, Integer> stats = new HashMap<>();
        // Lógica simplificada de estadísticas
        return stats;
    }

    // Helper para reconstruir objetos
    private Trabajo reconstruirTrabajo(ResultSet rs) throws Exception {
        String mat = rs.getString("vehiculo_matricula");
        String dni = rs.getString("cliente_dni");
        LocalDate ini = rs.getDate("fecha_inicio").toLocalDate();
        String tipo = rs.getString("tipo_trabajo");

        // BUSCA los objetos reales en la base de datos en lugar de usar "dummy"
        Vehiculo v = Vehiculos.getInstancia().buscar(Vehiculo.get(mat));
        Cliente c = Clientes.getInstancia().buscar(Cliente.get(dni));

        // Si alguno no existe, no podemos reconstruir el trabajo
        if (v == null || c == null) {
            return null;
        }

        Trabajo t = ("REVISION".equals(tipo)) ? new Revision(c, v, ini) : new Mecanico(c, v, ini);

        int horas = rs.getInt("horas");
        if (horas > 0) t.anadirHoras(horas);

        // Controlar nulos en la fecha de fin (si el trabajo está abierto)
        Date fin = rs.getDate("fecha_fin");
        if (fin != null) {
            t.cerrar(fin.toLocalDate());
        }

        return t;
    }
}
