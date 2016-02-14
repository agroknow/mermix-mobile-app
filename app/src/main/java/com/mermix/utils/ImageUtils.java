package com.mermix.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 10/01/2016
 * Description:
 * Image Utilities class
 *
 * check http://stackoverflow.com/a/21754321/3441616
 */
public class ImageUtils {

	public static String getImageBase64(String imagePath) {
		String base64String = "";
		byte[] bytes;
		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			InputStream inputStream = new FileInputStream(imagePath);
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
			bytes = output.toByteArray();
			base64String = Base64.encodeToString(bytes, Base64.DEFAULT);
		} catch (IOException e) {
			Common.logError("IOException @ Common getImageBase64:" + e.getMessage());
		}
		return base64String;
	}

	public static String bitmap2Base64(Bitmap bitmap) {
		String base64String = "";
		if(bitmap != null){
			ByteArrayOutputStream output = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output);
			byte[] byteArray = output.toByteArray();
			base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);
		}
		return base64String;
	}

	public static Bitmap configureBitmapSamplingRotation(String selectedImagePath)throws IOException{
		Common.log("ImageUtils configureBitmapSamplingRotation");
		int MAX_HEIGHT = 1024;
		int MAX_WIDTH = 1024;
		Bitmap bitmap;
		BitmapFactory.Options options = new BitmapFactory.Options();

		// First decode with inJustDecodeBounds=true to check dimensions
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(selectedImagePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
		bitmap = rotateImageIfRequired(bitmap, selectedImagePath);

		return bitmap;
	}

	public static Bitmap configureBitmapFromInputStream(InputStream inputStream){
		Common.log("ImageUtils configureBitmapFromInputStream");
		Bitmap bitmap;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 3;
		bitmap = BitmapFactory.decodeStream(inputStream, null, options);
		Common.log("bitmap size: " + Integer.toString(bitmap.getByteCount()) + " bytes");
		return bitmap;
	}

	/**
	 *
	 * @param img					the image bitmap
	 * @param selectedImagePath 	the image path
	 * @return						The resulted Bitmap after manipulation
	 */
	public static Bitmap rotateImageIfRequired(Bitmap img, String selectedImagePath) throws IOException {
		Common.log("ImageUtils rotateImageIfRequired");
		ExifInterface ei = new ExifInterface(selectedImagePath);
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

		Common.log("orientation: "+Integer.toString(orientation));
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				return rotateImage(img, 90);
			case ExifInterface.ORIENTATION_ROTATE_180:
				return rotateImage(img, 180);
			case ExifInterface.ORIENTATION_ROTATE_270:
				return rotateImage(img, 270);
			default:
				return img;
		}
	}

	public static Bitmap rotateImage(Bitmap img, int degree) {
		Common.log("ImageUtils rotateImage");
		Matrix matrix = new Matrix();
		Common.log("degree: "+Integer.toString(degree));
		matrix.postRotate(degree);
		Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
		img.recycle();
		return rotatedImg;
	}

	/**
	 * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
	 * bitmaps using the decode methods from {@link BitmapFactory}. This implementation calculates
	 * the closest inSampleSize that will result in the final decoded bitmap having a width and
	 * height equal to or larger than the requested width and height. This implementation does not
	 * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
	 * results in a larger bitmap which isn't as useful for caching purposes.
	 *
	 * @param options	An options object with out params already populated (run through a decode
	 *                  method with inJustDecodeBounds==true
	 * @param reqWidth	The requested width of the resulting bitmap
	 * @param reqHeight	The requested height of the resulting bitmap
	 * @return	The value to be used for inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		Common.log("ImageUtils calculateInSampleSize");
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee a final image
			// with both dimensions larger than or equal to the requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down further
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		Common.log("inSampleSize: " + Integer.toString(inSampleSize));
		return inSampleSize;
	}
}
