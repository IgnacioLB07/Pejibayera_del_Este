
package com.pejibayeraEste.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {

    @Value("${firebase.storage.bucket}")
    private String bucketName;

    @Value("${firebase.storage.path}")
    private String storagePath;

    private final Storage storage;

    private static final long MAX_SIZE = 2L * 1024 * 1024;
    private static final int MAX_DIMENSION = 1200;
    private static final long MAX_PIXELS = 20_000_000L;

    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }

    // Sube una imagen de producto a Firebase.
    public String uploadImage(
            MultipartFile localFile, String folder, Integer id) throws IOException {

        if (localFile == null || localFile.isEmpty()) {
            return null;
        }

        if (id == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }

        if (localFile.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "La imagen no puede superar los 2 MB.");
        }

        String mimeType = localFile.getContentType();

        if (mimeType == null
                || (!mimeType.equalsIgnoreCase("image/jpeg")
                && !mimeType.equalsIgnoreCase("image/png"))) {
            throw new IllegalArgumentException(
                    "Solo se permiten imágenes JPG o PNG.");
        }

        BufferedImage original = ImageIO.read(localFile.getInputStream());

        if (original == null) {
            throw new IllegalArgumentException(
                    "El archivo seleccionado no es una imagen válida.");
        }

        if ((long) original.getWidth() * original.getHeight() > MAX_PIXELS) {
            throw new IllegalArgumentException(
                    "La resolución de la imagen es demasiado grande.");
        }

        byte[] imagenOptimizada = optimizarImagen(original);

        // Nombre consistente de 14 dígitos, como en el ejemplo del profesor.
        String fileName = "img" + getFormattedNumber(id) + ".jpg";

        return uploadToFirebase(imagenOptimizada, folder, fileName);
    }

    // Redimensiona y comprime la imagen antes de subirla.
    private byte[] optimizarImagen(BufferedImage original) throws IOException {

        int ancho = original.getWidth();
        int alto = original.getHeight();

        double escala = Math.min(
                1.0,
                (double) MAX_DIMENSION / Math.max(ancho, alto));

        int nuevoAncho = Math.max(1, (int) (ancho * escala));
        int nuevoAlto = Math.max(1, (int) (alto * escala));

        BufferedImage imagen = new BufferedImage(
                nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = imagen.createGraphics();

        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, nuevoAncho, nuevoAlto);

            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            graphics.drawImage(
                    original, 0, 0, nuevoAncho, nuevoAlto, null);
        } finally {
            graphics.dispose();
        }

        Iterator<ImageWriter> escritores =
                ImageIO.getImageWritersByFormatName("jpeg");

        if (!escritores.hasNext()) {
            throw new IOException("No se encontró el escritor JPEG.");
        }

        ImageWriter escritor = escritores.next();

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream();
             ImageOutputStream imagenSalida =
                     ImageIO.createImageOutputStream(salida)) {

            escritor.setOutput(imagenSalida);

            ImageWriteParam parametros = escritor.getDefaultWriteParam();

            if (parametros.canWriteCompressed()) {
                parametros.setCompressionMode(
                        ImageWriteParam.MODE_EXPLICIT);
                parametros.setCompressionQuality(0.78f);
            }

            escritor.write(
                    null, new IIOImage(imagen, null, null), parametros);

            imagenSalida.flush();

            return salida.toByteArray();

        } finally {
            escritor.dispose();
        }
    }

    // Sube el archivo al almacenamiento de Firebase.
    private String uploadToFirebase(
            byte[] file, String folder, String fileName) throws IOException {

        String rutaBase = storagePath.replaceAll("^/+|/+$", "");
        String carpeta = folder.replaceAll("^/+|/+$", "");

        String objectName = rutaBase + "/" + carpeta + "/" + fileName;

        String token = UUID.randomUUID().toString();

        BlobId blobId = BlobId.of(bucketName, objectName);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/jpeg")
                .setCacheControl("public, max-age=3600")
                .setMetadata(Map.of(
                        "firebaseStorageDownloadTokens", token))
                .build();

        // Si el producto ya tenía imagen, se reemplaza el objeto anterior.
        storage.create(blobInfo, file);

        String nombreCodificado = URLEncoder.encode(
                objectName, StandardCharsets.UTF_8).replace("+", "%20");

        return "https://firebasestorage.googleapis.com/v0/b/"
                + bucketName
                + "/o/" + nombreCodificado
                + "?alt=media&token=" + token;
    }

    // Genera un número de 14 dígitos con ceros a la izquierda.
    private String getFormattedNumber(long id) {
        return String.format("%014d", id);
    }
}
