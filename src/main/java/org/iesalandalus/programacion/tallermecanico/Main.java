package org.iesalandalus.programacion.tallermecanico;

import javafx.util.Pair;
import org.iesalandalus.programacion.tallermecanico.controlador.Controlador;
import org.iesalandalus.programacion.tallermecanico.controlador.IControlador;
import org.iesalandalus.programacion.tallermecanico.modelo.FabricaModelo;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.FabricaFuenteDatos;
import org.iesalandalus.programacion.tallermecanico.vista.FabricaVista;

public class Main {

    public static void main(String[] args) throws Exception {
        // 1. Procesar los argumentos de entrada
        Pair<FabricaVista, FabricaFuenteDatos> fabricas = procesarArgumentos(args);

        System.out.println("Iniciando aplicación con fuente de datos: " + fabricas.getValue());
        System.out.println("Iniciando aplicación con vista: " + fabricas.getKey());

        // 2. Crear el controlador con el Modelo (Cascada), la Fuente de Datos y la Vista
        IControlador controlador = new Controlador(FabricaModelo.CASCADA, fabricas.getValue(), fabricas.getKey());

        // 3. Iniciar la aplicación
        controlador.comenzar();
    }

    private static Pair<FabricaVista, FabricaFuenteDatos> procesarArgumentos(String[] args) {
        // Valores por defecto
        FabricaVista fabricaVista = FabricaVista.VENTANAS;

        // CAMBIO AQUÍ: Por defecto ahora es MYSQL
        FabricaFuenteDatos fabricaFuenteDatos = FabricaFuenteDatos.MYSQL;

        for (String argumento : args) {
            // Selección de VISTA
            if (argumento.equalsIgnoreCase("-vventanas")) {
                fabricaVista = FabricaVista.VENTANAS;
            } else if (argumento.equalsIgnoreCase("-vtexto")) {
                fabricaVista = FabricaVista.TEXTO;
            }
            // Selección de FUENTE DE DATOS (Opcional, ya que MySQL es el defecto)
            else if (argumento.equalsIgnoreCase("-fdficherosxml")) {
                fabricaFuenteDatos = FabricaFuenteDatos.FICHEROS_XML;
            } else if (argumento.equalsIgnoreCase("-fdficherosjson")) {
                fabricaFuenteDatos = FabricaFuenteDatos.FICHEROS_JSON;
            } else if (argumento.equalsIgnoreCase("-fdmysql")) {
                fabricaFuenteDatos = FabricaFuenteDatos.MYSQL;
            }
        }

        return new Pair<>(fabricaVista, fabricaFuenteDatos);
    }
}