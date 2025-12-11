package org.iesalandalus.programacion.tallermecanico.modelo.negocio;


import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json.FuenteDatosFicherosJSON;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficherosxml.FuenteDatosFicherosXML;

public enum FabricaFuenteDatos {

    // Opción 1: Persistencia XML
    FICHEROS_XML {
        @Override
        public IFuenteDatos crear() {
            return new FuenteDatosFicherosXML();
        }
    },

    FICHEROS_JSON {
        @Override
        public IFuenteDatos crear() {
            return new FuenteDatosFicherosJSON();
        }
    };

    public abstract IFuenteDatos crear();
}