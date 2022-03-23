package com.mach.core.util;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRReader {

    private static final Logger logger = LoggerFactory.getLogger(QRReader.class);

    private QRReader() {
    	// Private constructor so class cannot be instantiated
    }
    
    public static String readQRCode(byte[] image) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(image);
        BufferedImage imageBuffer = ImageIO.read(stream);

        LuminanceSource source = new BufferedImageLuminanceSource(imageBuffer);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        String result;
		try {
			QRCodeReader zxingReader = new QRCodeReader();
			result = zxingReader.decode(bitmap).getText();
			logger.info("QR read with zxing {}", result);
		} catch (NotFoundException | ChecksumException | FormatException e) {
			result = null;
			logger.info("Could not read QR with zxing", e);
		}
        return result;
    }
    
    @Attachment(value = "Image converted to monochrome", type = "image/png")
    public static byte[] convertToBlackAndWhite(byte[] image) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(image);
        BufferedImage imageBuffer = ImageIO.read(stream);
        
        BufferedImage bwImageBuffer = new BufferedImage(
        		imageBuffer.getWidth(),
        		imageBuffer.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);
        
        Graphics2D g2d = bwImageBuffer.createGraphics();
        g2d.drawImage(imageBuffer, 0, 0, null);
        g2d.dispose();

    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ImageIO.write(bwImageBuffer, "png", baos);
    	return baos.toByteArray();
    }

}
