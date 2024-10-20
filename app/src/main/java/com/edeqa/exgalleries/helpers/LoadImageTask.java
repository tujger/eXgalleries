package com.edeqa.exgalleries.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
	private final ImageView bmImage;
	private int dWidth, dHeight, dMode;
	private boolean dCache = false;
	private File mFile;

	final public static int CENTER = 0;
	final public static int CENTER_CROP = 1;
	final public static int CENTER_INSIDE = 2;

	public LoadImageTask(ImageView bmImage) {
		this.bmImage = bmImage;
	}

	public LoadImageTask setCache(int height, int width, int mode, File file) {
		dHeight = height;
		dWidth = width;
		dMode = mode;
		dCache = true;
		mFile = file;
		return this;
	}

	protected Bitmap doInBackground(String... urls) {

		Bitmap bmp = loadBitmapFromUrl(urls[0]);

		if (dCache) {
			mFile.getParentFile().mkdirs();
			saveImage(bmp, mFile.getAbsolutePath() + "Raw");

			new File(mFile.getAbsolutePath() + "Thumb").delete();
			if (dMode > 0) {
				Bitmap nbmp=loadImage(mFile.getAbsolutePath() + "Raw");
				scaleCenterCrop(nbmp, dHeight, dWidth);
				saveImage(nbmp, mFile.getAbsolutePath() + "Thumb");
				bmp.recycle();
				bmp=nbmp;
			}
		}


		return bmp;
	}

	protected void onPostExecute(Bitmap result) {
		bmImage.setImageBitmap(result);
	}

	private Bitmap loadBitmapFromUrl(String url) {

		String urldisplay = url;
		Bitmap mBmp = null;
		try {
			InputStream in = new java.net.URL(urldisplay).openStream();
			mBmp = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

		return mBmp;
	}

	private boolean saveImage(Bitmap bmp, String filename) {
		// private boolean saveImage(String imageUrl, String destinationFile) {
		OutputStream out = null;
		boolean res = false;
		try {
			out = new FileOutputStream(filename);
			res = bmp.compress(Bitmap.CompressFormat.JPEG, 60, out);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	private Bitmap loadImage(String filename) {
		Bitmap bmp = null;
		if (new File(filename).exists())
			bmp = BitmapFactory.decodeFile(filename);
		return bmp;
	}

	public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		// Compute the scaling factors to fit the new height and width,
		// respectively.
		// To cover the final image, the final scaling will be the bigger
		// of these two.
		float xScale = (float) newWidth / sourceWidth;
		float yScale = (float) newHeight / sourceHeight;
		float scale = Math.max(xScale, yScale);

		// Now get the size of the source bitmap when scaled
		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		// Let's find out the upper left coordinates if the scaled bitmap
		// should be centered in the new size give by the parameters
		float left = (newWidth - scaledWidth) / 2;
		float top = (newHeight - scaledHeight) / 2;

		// The target rectangle for the new, scaled version of the source bitmap
		// will now
		// be
		RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		// Finally, we create a new bitmap of the specified size and draw our
		// new,
		// scaled bitmap onto it.
		Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(source, null, targetRect, null);

		return dest;
	}
}
