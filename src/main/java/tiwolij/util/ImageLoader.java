package tiwolij.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

public class ImageLoader {

	public static byte[] getBytes(URL url, Integer height) {
		byte[] result = new byte[0];

		try {
			byte[] bytes = IOUtils.toByteArray(url.openStream());
			BufferedImage input = ImageIO.read(new ByteArrayInputStream(bytes));
			Integer width = (height * input.getWidth()) / input.getHeight();
			Image output = input.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			buffer.getGraphics().drawImage(output, 0, 0, new Color(0, 0, 0), null);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(buffer, "jpg", stream);
			result = stream.toByteArray();
		} catch (Exception e) {
		}

		return result;
	}

}
