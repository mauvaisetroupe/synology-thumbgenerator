package com.mauvaisetroupe.synology;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import org.imgscalr.Scalr.Rotation;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class ImageResizer {
	
	
	public void walk(String pathToExplore) throws IOException {
		Consumer<Path> myConsumer = new Consumer<Path>() {
			@Override
			public void accept(Path t) {
				// System.out.println(t);
				// avoid @eaDir in @eaDir

				if (t.toString().contains("@eaDir")) {
					return;
				}

				File eaDir = new File(t.getParent().toString(), "@eaDir");
				File jpgThumbsFolder = new File(eaDir, t.getFileName().toString());
				if (!jpgThumbsFolder.exists())
					jpgThumbsFolder.mkdirs();
				
				if (!jpgThumbsFolder.exists())
					throw new RuntimeException("Cannot create folder " + jpgThumbsFolder);

				List<Integer> 	sizes 			= Arrays.asList(1280, 640, 320, 512, 120);
				List<String> 	names 			= Arrays.asList("XL", "B", "M", "PREVIEW", "S");
				List<Boolean> 	resizeOnMin 	= Arrays.asList(true,false,true,false,false);

				for (int i = 0; i < names.size(); i++) {
					try {
						File f = new File(jpgThumbsFolder, "SYNOPHOTO_THUMB_" + names.get(i) + ".jpg");
						if (!f.exists()) {
							resizeAndRedress(t.toString(), f.getAbsolutePath(), sizes.get(i), resizeOnMin.get(i));
							System.out.println(f + " created.");
						}
					}

					catch (Throwable e) {
						e.printStackTrace();
					}

				}
			}

		};

		Files.walk(Paths.get(pathToExplore)).filter(Files::isRegularFile).filter(f -> f.toString().endsWith("jpg")).forEach(myConsumer);
		//Files.walk(Paths.get("D://TEST-thumbnails/echantillon1")).filter(Files::isRegularFile).filter(f -> f.toString().endsWith("jpg")).forEach(myConsumer);
		// que faire avec les .JPG au lieu de .jpg ?
		// que faire avec les .JPEG au lieu de .jpg ?
		// f.toString().toLowerCase().endsWith(".jpg")
	}
	
	public void resizeAndRedress(String inputImagePath, String outputImagePath, int newSize, Boolean resizeOnMin) throws IOException, ImageProcessingException {

		// reads input image
		File inputFile = new File(inputImagePath);
		BufferedImage inputImage = ImageIO.read(inputFile);
		if (inputImage==null) {
			System.out.println("XXXXX PB with file " +  inputImagePath);
			return;
		}

		// creates output image
		Scalr.Mode scaleMode = Scalr.Mode.AUTOMATIC;

		int maxsize;
		if (!resizeOnMin) {
			// max -> newSize
			maxsize = newSize;
		}
		else {
			// min -> newSize
			int max = Math.max(inputImage.getWidth(), inputImage.getHeight());
			int min = Math.min(inputImage.getWidth(), inputImage.getHeight());
			// min | newSize
			// max | ?
			maxsize = max*newSize/min;
		}
		BufferedImage outputImage = Scalr.resize(inputImage, Scalr.Method.QUALITY, scaleMode, maxsize);

		Metadata metadata = ImageMetadataReader.readMetadata(new File(inputImagePath));
		ExifIFD0Directory exifIFD0Directory = (ExifIFD0Directory) metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

		int orientation = 1;
		try {
			orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
		} catch (Exception ex) {
			System.out.println("tag orientation not found");
		}
		switch (orientation) {
		case 1:
			break;
		case 2: // Flip X
			outputImage = Scalr.rotate(outputImage, Rotation.FLIP_HORZ);
			break;
		case 3: // PI rotation
			outputImage = Scalr.rotate(outputImage, Rotation.CW_180);
			break;
		case 4: // Flip Y
			outputImage = Scalr.rotate(outputImage, Rotation.FLIP_VERT);
			break;
		case 5: // - PI/2 and Flip X
			outputImage = Scalr.rotate(outputImage, Rotation.CW_90);
			outputImage = Scalr.rotate(outputImage, Rotation.FLIP_HORZ);
			break;
		case 6: // -PI/2 and -width
			outputImage = Scalr.rotate(outputImage, Rotation.CW_90);
			break;
		case 7: // PI/2 and Flip
			outputImage = Scalr.rotate(outputImage, Rotation.CW_90);
			outputImage = Scalr.rotate(outputImage, Rotation.FLIP_VERT);
			break;
		case 8: // PI / 2
			outputImage = Scalr.rotate(outputImage, Rotation.CW_270);
			break;
		default:
			break;
		}


		// extracts extension of output file
		String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);
		// writes to output file
		ImageIO.write(outputImage, formatName, new File(outputImagePath));

	}

	/**
	 * 
	 * Test resizing images
	 * 
	 *
	 * 
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {

		ImageResizer imageResizer = new ImageResizer();
		if(args.length != 1 ) { 
			   throw new RuntimeException("Please provide folder to explore as argument");
			}
		String filePathString = args[0];
		File f = new File(filePathString);
		if(args.length != 1 || ! f.exists() || !f.isDirectory()) { 
		   throw new RuntimeException("Please provide folder to explore - pb with " + f.getAbsolutePath());
		}
		imageResizer.walk(filePathString);

	}
}