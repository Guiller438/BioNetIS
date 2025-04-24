package com.bionet;

import java.io.*;
import java.text.SimpleDateFormat;

public class ArchivoValidator {

    public boolean esValido(File archivo) {
        boolean tieneLineas = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numLinea = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);

            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();
                if (linea.isEmpty()) {
                    System.out.println("Línea vacía encontrada en la línea " + numLinea);
                    return false;
                }

                String[] campos = linea.split(",");
                if (campos.length != 6) {
                    System.out.println("Número incorrecto de columnas en línea " + numLinea);
                    return false;
                }

                for (String campo : campos) {
                    if (campo == null || campo.trim().isEmpty()) {
                        System.out.println("Campo vacío en línea " + numLinea);
                        return false;
                    }
                }

                // Validar formato de fecha
                try {
                    sdf.parse(campos[5]);
                } catch (Exception e) {
                    System.out.println("Formato de fecha incorrecto en línea " + numLinea);
                    return false;
                }

                tieneLineas = true;
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return false;
        }

        if (!tieneLineas) {
            System.out.println("El archivo está vacío.");
            return false;
        }

        return true;
    }
}
