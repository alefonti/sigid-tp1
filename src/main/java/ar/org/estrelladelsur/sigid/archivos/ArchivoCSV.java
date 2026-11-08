package ar.org.estrelladelsur.sigid.archivos;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase ABSTRACTA y genérica para leer y escribir archivos CSV.
 *
 * Resuelve una sola vez lo que es común a cualquier archivo del sistema
 * (crear la carpeta, abrir y cerrar el archivo, escribir el encabezado,
 * separar cada línea en campos, numerar los errores). Cada subclase
 * define sólo sus columnas y cómo pasar de un objeto a un arreglo de
 * campos y viceversa.
 *
 * Se usa punto y coma como separador porque es el que Excel y
 * LibreOffice Calc esperan con la configuración regional de Argentina.
 */
public abstract class ArchivoCSV<T> {

    public static final String SEPARADOR = ";";

    /** Resultado de una lectura: lo que se pudo leer y los errores por línea. */
    public record Lectura<T>(List<T> registros, List<String> errores) { }

    /** Nombres de las columnas, en orden. */
    protected abstract String[] encabezado();

    /** Convierte un objeto en el arreglo de campos de una línea. */
    protected abstract String[] aCampos(T objeto);

    /** Convierte los campos de una línea en un objeto; valida cada dato. */
    protected abstract T desdeCampos(String[] campos) throws DatoInvalidoException;

    // ================================================================ escritura

    /** Escribe el encabezado y una línea por objeto. Devuelve las filas escritas. */
    public int escribir(Path destino, List<T> datos) {
        try {
            Path carpeta = destino.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            // try-with-resources: el archivo se cierra aunque falle una escritura
            try (BufferedWriter out = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
                out.write('﻿');                 // BOM para que Excel reconozca las tildes
                out.write(unir(encabezado()));
                out.newLine();
                for (T dato : datos) {
                    out.write(unir(aCampos(dato)));
                    out.newLine();
                }
            }
            return datos.size();
        } catch (IOException e) {
            throw new ArchivoException("No se pudo escribir el archivo " + destino, e);
        }
    }

    // ================================================================ lectura

    /**
     * Lee el archivo línea por línea. Una línea con errores no detiene la
     * lectura: se anota en la lista de errores y se sigue con la próxima.
     */
    public Lectura<T> leer(Path origen) {
        List<T>      registros = new ArrayList<>();
        List<String> errores   = new ArrayList<>();
        String[]     columnas  = encabezado();

        try (BufferedReader in = Files.newBufferedReader(origen, StandardCharsets.UTF_8)) {
            String linea = in.readLine();
            if (linea == null) {
                errores.add("El archivo está vacío.");
                return new Lectura<>(registros, errores);
            }
            int nro = 1;                               // la línea 1 es el encabezado
            while ((linea = in.readLine()) != null) {
                nro++;
                if (linea.isBlank()) continue;
                String[] campos = linea.split(SEPARADOR, -1);   // -1: conserva campos vacíos
                if (campos.length != columnas.length) {
                    errores.add("Línea " + nro + ": se esperaban " + columnas.length
                              + " campos y hay " + campos.length + ".");
                    continue;
                }
                for (int i = 0; i < campos.length; i++) {
                    campos[i] = campos[i].trim();
                }
                try {
                    registros.add(desdeCampos(campos));
                } catch (DatoInvalidoException | IllegalArgumentException e) {
                    errores.add("Línea " + nro + ": " + e.getMessage());
                }
            }
            return new Lectura<>(registros, errores);
        } catch (NoSuchFileException e) {
            throw new ArchivoException("No existe el archivo " + origen, e);
        } catch (IOException e) {
            throw new ArchivoException("No se pudo leer el archivo " + origen, e);
        }
    }

    // ================================================================ utilidades

    private static String unir(String[] campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) sb.append(SEPARADOR);
            String c = campos[i] == null ? "" : campos[i];
            sb.append(c.replace(SEPARADOR, ","));     // evita romper la estructura
        }
        return sb.toString();
    }
}
