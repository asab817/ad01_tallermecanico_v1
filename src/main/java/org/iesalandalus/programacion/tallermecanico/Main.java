package org.iesalandalus.programacion.tallermecanico;

import javafx.util.Pair;
import org.iesalandalus.programacion.tallermecanico.controlador.Controlador;
import org.iesalandalus.programacion.tallermecanico.controlador.IControlador;
import org.iesalandalus.programacion.tallermecanico.modelo.FabricaModelo;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.FabricaFuenteDatos;
import org.iesalandalus.programacion.tallermecanico.vista.FabricaVista;

public class Main {

    public static void main(String[] args) {
        // Envolvemos todo en un try-catch para capturar errores de conexión o ficheros
        try {
            Pair<FabricaVista, FabricaFuenteDatos> fabricas = procesarArgumentos(args);

            // Esta línea daba error porque le faltaba el control de excepciones
            IControlador controlador = new Controlador(FabricaModelo.CASCADA, fabricas.getValue(), fabricas.getKey());

            controlador.comenzar();

        } catch (Exception e) {
            System.out.println("Error crítico al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace(); // Esto te ayudará a ver dónde falla exactamente si hay error
        }
    }

    private static Pair<FabricaVista, FabricaFuenteDatos> procesarArgumentos(String[] args) {
        FabricaVista fabricaVista = FabricaVista.VENTANAS;
        // Por defecto suele ser FICHEROS (JSON o XML), asegúrate de elegir uno válido
        FabricaFuenteDatos fabricaFuenteDatos = FabricaFuenteDatos.FICHEROS_JSON;

        for (String argumento : args) {
            if (argumento.equalsIgnoreCase("-vventanas")) {
                fabricaVista = FabricaVista.VENTANAS;
            } else if (argumento.equalsIgnoreCase("-vtexto")) {
                fabricaVista = FabricaVista.TEXTO;
            } else if (argumento.equalsIgnoreCase("-fdficherosxml")) {
                fabricaFuenteDatos = FabricaFuenteDatos.FICHEROS_XML;
            } else if (argumento.equalsIgnoreCase("-fdficherosjson")) {
                fabricaFuenteDatos = FabricaFuenteDatos.FICHEROS_JSON;
            } else if (argumento.equalsIgnoreCase("-fdmysql")) {
                fabricaFuenteDatos = FabricaFuenteDatos.MYSQL;
            } else if (argumento.equalsIgnoreCase("-fdmongodb")) {
                fabricaFuenteDatos = FabricaFuenteDatos.MONGODB;
            }
        }
        return new Pair<>(fabricaVista, fabricaFuenteDatos);
    }
}