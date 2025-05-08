package com.edisa.formacion.mayo2025;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class MainQR {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java Main \"Texto\" \"ruta\\salida.jpg\" \"Formato (QR_CODE, EAN_13, CODE_128...)\"");
            return;
        }

        String textQR = args[0];
        String finalPath = args[1];
        String textFormat = args[2].toUpperCase();

        int width = 300;
        int heigh = 300;

        try {
            BarcodeFormat format = BarcodeFormat.valueOf(textFormat);
            BitMatrix matrix = new MultiFormatWriter().encode(textQR, format, width, heigh);
            Path path = FileSystems.getDefault().getPath(finalPath);
            MatrixToImageWriter.writeToPath(matrix, "jpg", path);
            System.out.println("Código generado en formato " + format + " correctamente en: " + finalPath);
        } catch (IllegalArgumentException e) {
            System.err.println("Formato no válido. Usa uno de los siguientes:");
            for (BarcodeFormat bf : BarcodeFormat.values()) {
                System.out.println("- " + bf.name());
            }
        } catch (Exception e) {
            System.err.println("Error al generar el código QR: " + e.getMessage());
        }
    }
}