package com.edisa.formacion.mayo2025;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java Main \"Texto para QR\" \"ruta\\al\\archivo.jpg\"");
            return;
        }

        String textoQR = args[0];
        String rutaSalida = args[1];

        int ancho = 300;
        int alto = 300;

        try {
            BitMatrix matrix = new MultiFormatWriter().encode(textoQR, BarcodeFormat.QR_CODE, ancho, alto);
            Path path = FileSystems.getDefault().getPath(rutaSalida);
            MatrixToImageWriter.writeToPath(matrix, "jpg", path);
            System.out.println("Código QR generado correctamente en: " + rutaSalida);
        } catch (Exception e) {
            System.err.println("Error al generar el código QR: " + e.getMessage());
        }
    }
}