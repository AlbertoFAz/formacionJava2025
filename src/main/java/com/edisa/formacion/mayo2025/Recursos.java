package com.edisa.formacion.mayo2025;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Path("/api")
public class Recursos {

    @GET
    @Path("/saludo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar(@QueryParam("nombre") String nombre,
                            @QueryParam("apellido") String apellido,
                            @QueryParam("edad") int edad) {

        return Response.status(404).build();
    }

    @POST
    @Path("/saludo/post")
    @Produces(MediaType.APPLICATION_JSON)
    public String saludar_post(@QueryParam("nombre") String nombre,
                               @QueryParam("apellido") String apellido,
                               @QueryParam("edad") int edad) {

        return "Hola desde el metodo POST, " + nombre + " " + apellido + ". Tienes " + edad + " años.";
    }

    @GET
    @Path("/codabar/generar")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response generarCodigoBarras(@QueryParam("texto") String texto,
                                        @QueryParam("formato_codigo_barras") String formatoCodigoBarras){
        try {
            BarcodeFormat format = BarcodeFormat.valueOf(formatoCodigoBarras.toUpperCase());

            BitMatrix matrix = new MultiFormatWriter().encode(texto, format, 300, 300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            return Response.ok(imageBytes)
                    .header("Content-Disposition", "attachment; filename=\"codigo.jpg\"")
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(("Formato no válido: " + formatoCodigoBarras).getBytes())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(("Error generando código: " + e.getMessage()).getBytes())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

    }

    @POST
    @Path("/codabar/detectar")
    @Consumes("image/jpeg")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detectarCodigosBarras(InputStream imagenStream) {
        try {
            BufferedImage imagen = ImageIO.read(imagenStream);
            if (imagen == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Collections.singletonMap("error", "Imagen no válida"))
                        .build();
            }

            LuminanceSource source = new BufferedImageLuminanceSource(imagen);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Reader lector = new MultiFormatReader();
            MultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(lector);

            Result[] resultados = multiReader.decodeMultiple(bitmap);

            List<Map<String, String>> codigos = new ArrayList<>();
            for (Result r : resultados) {
                Map<String, String> info = new HashMap<>();
                info.put("texto", r.getText());
                info.put("formato", r.getBarcodeFormat().toString());
                codigos.add(info);
            }

            return Response.ok(codigos).build();

        } catch (NotFoundException e) {
            return Response.ok(Collections.emptyList()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .build();
        }
    }


}
