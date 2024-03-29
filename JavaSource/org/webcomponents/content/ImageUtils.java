package org.webcomponents.content;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.springframework.web.multipart.MultipartFile;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageUtils {
	
	public static BufferedImage getImage(MultipartFile resource) throws IOException {
		InputStream is = resource.getInputStream();
		try {
			return ImageIO.read(is);
		} finally {
			is.close();
		}
	}
	
	public static PictureMetaData getMetaData(MultipartFile resource) throws IOException {
		InputStream is = resource.getInputStream();
		try {
			ImageInfo meta = Sanselan.getImageInfo(is, resource.getOriginalFilename());
			PictureMetaData rv = new PictureMetaData();
			rv.setSize(resource.getSize());
			rv.setContentType(meta.getMimeType());
			rv.setResolution(meta.getPhysicalWidthDpi());
			rv.setWidth(meta.getWidth());
			rv.setHeight(meta.getHeight());
			return rv;
		} catch (ImageReadException e) {
			throw new IOException(e);
		} finally {
			is.close();
		}
	}
	
	public static BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage rv = new BufferedImage(width, height, image.getType());
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Graphics2D graphics2D = ge.createGraphics(rv);
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		return rv;
	}

	
	public static BufferedImage scale(BufferedImage source, int sourceDpi, int destDpi) {
		if(sourceDpi == destDpi) {
			return null;
		}
		int width = (int) (source.getWidth() * (double) destDpi / sourceDpi);
		int height = (int) (source.getHeight() * (double) destDpi / sourceDpi);
		
		return resize(source, width, height);
	}
	
	public static BufferedImage scaleAndShrink(BufferedImage source, int sourceDpi, int destDpi, double percentage) {
		if(sourceDpi == destDpi) {
			return null;
		}
		int width = (int) (source.getWidth() * (double) destDpi / sourceDpi * percentage);
		int height = (int) (source.getHeight() * (double) destDpi / sourceDpi * percentage);
		
		return resize(source, width, height);
	}
	
	public static BufferedImage shrink(BufferedImage image, int maxWidth, int maxHeight) {
		if (maxWidth == -1 && maxHeight == -1) {
			return image;
		}

		int width  = image.getWidth();
		int height = image.getHeight();
		
		double x_scale = maxWidth == -1 ? 1.0 : (double) width / (double) maxWidth;
		double y_scale = maxHeight == -1 ? 1.0 : (double) height / (double) maxHeight;
		
		if (x_scale > 1 && x_scale >= y_scale) {
			width /= x_scale;
			height /= x_scale;
		}
		else if (y_scale > 1 && y_scale >= x_scale) {
			width /= y_scale;
			height /= y_scale;			
		}
		
		return resize(image, width, height);
	}

	public static void writeJpeg(BufferedImage image, OutputStream out, int dpi) throws ImageFormatException, IOException {
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
		param.setQuality(0.85f, false);
		param.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
		param.setXDensity(dpi);
		param.setYDensity(dpi);
		encoder.setJPEGEncodeParam(param);
		encoder.encode(image);
	}

	public static void writeJpeg(BufferedImage image, File dest, int dpi) throws ImageFormatException, IOException {
		File folder = dest.getParentFile();
		if(!folder.exists()) {
			folder.mkdirs();
		}
		OutputStream out = new FileOutputStream(dest);
		try {
			writeJpeg(image, out, dpi);
		} finally {
			out.close();
		}
	}
	
	public static void writeJpeg(BufferedImage image, File dest, int dpi, PictureMetaData meta) throws ImageFormatException, IOException {
		File folder = dest.getParentFile();
		if(!folder.exists()) {
			folder.mkdirs();
		}
		writeJpeg(image, dest, dpi);
		meta.setWidth(image.getWidth());
		meta.setHeight(image.getHeight());
		meta.setResolution(dpi);
		meta.setContentType("image/jpeg");
		meta.setSize(dest.length());
	}
}
