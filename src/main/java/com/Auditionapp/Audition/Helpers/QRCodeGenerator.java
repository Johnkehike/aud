package com.Auditionapp.Audition.Helpers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRCodeGenerator {


    public static byte[] generateQRCode(String data, int size) throws WriterException, IOException {

        // Set the QR code encoding parameters
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size);

        // Convert the bit matrix to a BufferedImage
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Convert the BufferedImage to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);

        return baos.toByteArray();
    }
}
