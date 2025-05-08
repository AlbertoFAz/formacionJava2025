package com.edisa.formacion.mayo2025;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;

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

}
